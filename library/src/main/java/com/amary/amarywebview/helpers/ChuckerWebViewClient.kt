package com.amary.amarywebview.helpers

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.amary.amarywebview.utils.isJson
import com.amary.amarywebview.utils.orEmpty
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ChuckerWebViewClient(
    private val isChuckerEnabled: Boolean,
    private val handleRequestsPayload: Boolean,
    private val interceptPreflight: Boolean,
    private val interceptAllHosts: Boolean,
    private val interceptHosts: Set<Regex>,
    private val interceptFileSchema: Boolean,
    private val interceptDataSchema: Boolean,
    private val interceptAllFileExtension: Boolean,
    private val interceptFileExtension: Set<String>,
    private val recorder: PayloadRecorder?,
    private val chuckerListener: ChuckerListener?,
    private val okHttpClient: OkHttpClient?
) {

    companion object{
        private const val javaScriptInject =
            "XMLHttpRequest.prototype.origOpen = XMLHttpRequest.prototype.open;\n" +
                    "XMLHttpRequest.prototype.open = function(method, url, async, user, password) {\n" +
                    "    // these will be the key to retrieve the payload\n" +
                    "    this.recordedMethod = method;\n" +
                    "    this.recordedUrl = url;\n" +
                    "    this.origOpen(method, url, async, user, password);\n" +
                    "};\n" +
                    "XMLHttpRequest.prototype.origSend = XMLHttpRequest.prototype.send;\n" +
                    "XMLHttpRequest.prototype.send = function(body) {\n" +
                    "    // interceptor is a Kotlin interface added in WebView\n" +
                    "    if(body) recorder.recordPayload(this.recordedMethod, this.recordedUrl, body);\n" +
                    "    this.origSend(body);\n" +
                    "};"
    }

    fun onPageStarted(view: WebView?) {
        if (!isChuckerEnabled) return
        if (handleRequestsPayload)
            view?.evaluateJavascript(javaScriptInject, null)
    }

    fun shouldInterceptRequest(
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if (!isChuckerEnabled) return null
        val payload = recorder?.getPayload(request?.method.orEmpty(), request?.url.toString())
        val req = request?.let { ChuckerWebResourceRequest(it, payload) }
        if (urlShouldBeHandledByWebView(req)) return null
        return handleRequestViaOkHttp(req)
    }

    private fun urlShouldBeHandledByWebView(request: ChuckerWebResourceRequest?): Boolean {
        if (!isChuckerEnabled) return true
        if (request == null) return true
        if (request.payload?.isJson() == false) return true

        when {
            !interceptPreflight && request.method == "OPTIONS" -> return true
            !handleRequestsPayload && request.method != "GET" && request.method != "OPTIONS" -> return true
            handleRequestsPayload && (request.method == "POST" || request.method == "PUT" || request.method == "PATCH") && !request.payload.isJson() -> return true
        }

        if (!interceptAllHosts && !interceptHosts.any {
                it.matches(request.url.host ?: "")
            }) return true

        request.url.lastPathSegment?.let {
            if (!interceptAllFileExtension && it.contains(".") && !interceptFileExtension.contains(
                    it.split(".")[1]
                )
            ) return true
        }

        if (interceptFileSchema && request.url.scheme == "file") return true

        if (interceptDataSchema && request.url.scheme == "data") return true

        return false
    }

    private fun handleRequestViaOkHttp(request: ChuckerWebResourceRequest?): WebResourceResponse? {
        if (!isChuckerEnabled) return null
        if (request == null) return null

        try {
            chuckerListener?.onHandleRequestViaOkHttp(request)

            val headersBuilder = Headers.Builder()
            request.requestHeaders.forEach {
                headersBuilder.add(it.key, it.value)
            }

            val req = Request.Builder()
                .url(request.url.toString())
                .headers(headersBuilder.build())
                .method(
                    request.method,
                    request.payload?.let { it.toRequestBody(it.toMediaTypeOrNull()) })
                .build()

            val call = okHttpClient?.newCall(req)

            val response = call?.execute()

            val headers = hashMapOf<String, String>()
            response?.headers?.names()?.forEach { key ->
                response.headers[key]?.let {
                    headers[key] = it
                }
            }

            val textHtmlValue = "text/plain"

            var reasonPhrase = response?.message
            if (reasonPhrase.isNullOrBlank()) {
                reasonPhrase = response?.code.toString()
            }

            return WebResourceResponse(
                response?.header("content-type", textHtmlValue)?.split(";")?.get(0) ?: textHtmlValue,
                response?.header("content-encoding", "utf-8"),
                response?.code.orEmpty(),
                reasonPhrase,
                headers,
                response?.body?.byteStream()
            )

        } catch (e: Exception) {
            chuckerListener?.onFailedOKHttpRequest(request, e)
            return null
        }
    }
}