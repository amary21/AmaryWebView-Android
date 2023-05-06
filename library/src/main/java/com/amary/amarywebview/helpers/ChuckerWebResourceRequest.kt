package com.amary.amarywebview.helpers

import android.net.Uri
import android.webkit.WebResourceRequest

class ChuckerWebResourceRequest(
    val request: WebResourceRequest,
    var payload: String? = null
): WebResourceRequest {
    override fun getUrl(): Uri {
        return request.url
    }

    override fun isForMainFrame(): Boolean {
        return request.isForMainFrame
    }

    override fun isRedirect(): Boolean {
        return isRedirect
    }

    override fun hasGesture(): Boolean {
        return request.hasGesture()
    }

    override fun getMethod(): String {
        return request.method
    }

    override fun getRequestHeaders(): MutableMap<String, String> {
        return request.requestHeaders
    }

    // in case this interface got extended just implement the new method/member and return it's value
    // ex
    // override fun newMethod(): MutableMap<String, String> {
    //     return request.newMethod()
    //}
}