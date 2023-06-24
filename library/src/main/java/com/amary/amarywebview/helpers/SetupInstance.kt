package com.amary.amarywebview.helpers

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient

object SetupInstance {
    private var okHttpClient: OkHttpClient? = null

    private val defaultOkHttpClient by lazy {
        val builder: OkHttpClient.Builder =
            OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(SyncCookieHandler()))
        builder.build()
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient){
        this.okHttpClient = okHttpClient
    }

    fun getOkHttpClient() = this.okHttpClient ?: defaultOkHttpClient
}