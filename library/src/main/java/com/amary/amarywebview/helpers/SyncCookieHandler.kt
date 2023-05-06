package com.amary.amarywebview.helpers

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.webkit.CookieManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.CookieHandler
import java.net.URI
import java.util.Collections

class SyncCookieHandler(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }
) : CookieHandler() {
    private val mCookieSaver: CookieSaver
    private var mCookieManager: CookieManager? = null

    private val context =
        Dispatchers.IO + Job() + exceptionHandler
    private val scope = CoroutineScope(context)

    @Throws(IOException::class)
    override fun get(uri: URI, headers: Map<String, List<String>>): Map<String, List<String>> {
        val cookieManager = cookieManager
            ?: return emptyMap()
        val cookies = cookieManager.getCookie(uri.toString())
        return if (TextUtils.isEmpty(cookies)) {
            emptyMap()
        } else Collections.singletonMap(
            COOKIE_HEADER,
            listOf(cookies)
        )
    }

    @Throws(IOException::class)
    override fun put(uri: URI, headers: Map<String?, List<String>>) {
        val url = uri.toString()
        for ((key, value) in headers) {
            if (key != null && isCookieHeader(key)) {
                addCookies(url, value)
            }
        }
    }

    fun clearCookies() {
        clearCookiesAsync()
    }

    private fun clearCookiesAsync() {
        val cookieManager = cookieManager
        cookieManager?.removeAllCookies { mCookieSaver.onCookiesModified() }
    }

    fun destroy() {
        scope.cancel()
    }

    fun addCookies(url: String, cookies: List<String>) {
        val cookieManager = cookieManager ?: return
        for (cookie in cookies) {
            addCookieAsync(url, cookie)
        }
        cookieManager.flush()
        mCookieSaver.onCookiesModified()
    }

    private fun addCookieAsync(url: String, cookie: String) {
        val cookieManager = cookieManager
        cookieManager?.setCookie(url, cookie, null)
    }

    private fun runInBackground(runnable: Runnable) {
        scope.launch {
            runnable.run()
        }
    }
    // We cannot catch MissingWebViewPackageException as it is in a private / system API
    // class. This validates the exception's message to ensure we are only handling this
    // specific exception.
    // https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/webkit/WebViewFactory.java#348
    // https://bugs.chromium.org/p/chromium/issues/detail?id=559720
    /**
     * Instantiating CookieManager in KitKat+ will load the Chromium task taking a 100ish ms so we do
     * it lazily to make sure it's done on a background thread as needed.
     */
    private val cookieManager: CookieManager?
        get() {
            if (mCookieManager == null) {
                mCookieManager = try {
                    CookieManager.getInstance()
                } catch (ex: IllegalArgumentException) {
                    // https://bugs.chromium.org/p/chromium/issues/detail?id=559720
                    return null
                } catch (exception: Exception) {
                    val message = exception.message
                    // We cannot catch MissingWebViewPackageException as it is in a private / system API
                    // class. This validates the exception's message to ensure we are only handling this
                    // specific exception.
                    // https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/webkit/WebViewFactory.java#348
                    return if (message != null
                        && (exception
                            .javaClass
                            .canonicalName
                                == "android.webkit.WebViewFactory.MissingWebViewPackageException")
                    ) {
                        null
                    } else {
                        throw exception
                    }
                }
            }
            return mCookieManager
        }

    /**
     * Responsible for flushing cookies to disk. Flushes to disk with a maximum delay of 30 seconds.
     * This class is only active if we are on API < 21.
     */
    private inner class CookieSaver {
        private val mHandler: Handler
        fun onCookiesModified() {

        }

        fun persistCookies() {
            mHandler.removeMessages(Companion.MSG_PERSIST_COOKIES)
            runInBackground {
                flush()
            }
        }

        private fun flush() {
            val cookieManager = cookieManager
            cookieManager?.flush()
        }

        init {
            mHandler = Handler(
                Looper.getMainLooper()
            ) { msg ->
                if (msg.what == MSG_PERSIST_COOKIES) {
                    persistCookies()
                    true
                } else {
                    false
                }
            }
        }
    }

    companion object {
        private const val VERSION_ZERO_HEADER = "Set-cookie"
        private const val VERSION_ONE_HEADER = "Set-cookie2"
        private const val COOKIE_HEADER = "Cookie"
        private const val MSG_PERSIST_COOKIES = 1

        private fun isCookieHeader(name: String): Boolean {
            return name.equals(VERSION_ZERO_HEADER, ignoreCase = true) || name.equals(
                VERSION_ONE_HEADER, ignoreCase = true
            )
        }
    }

    init {
        mCookieSaver = CookieSaver()
    }
}