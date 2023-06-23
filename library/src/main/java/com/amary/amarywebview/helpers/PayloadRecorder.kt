package com.amary.amarywebview.helpers

import android.webkit.JavascriptInterface
import androidx.annotation.Keep

class PayloadRecorder {
    private val payloadMap: MutableMap<String, String> =
        mutableMapOf()

    @JavascriptInterface
    @Keep
    fun recordPayload(
        method: String?,
        url: String?,
        payload: String?
    ) {
        payloadMap["${method?.lowercase()}-$url"] = payload ?: ""
    }

    fun getPayload(
        method: String,
        url: String
    ): String? =
        payloadMap["${method.lowercase()}-$url"]
}