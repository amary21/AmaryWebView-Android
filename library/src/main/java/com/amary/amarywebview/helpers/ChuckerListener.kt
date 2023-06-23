package com.amary.amarywebview.helpers

import android.webkit.WebResourceRequest

abstract class ChuckerListener {
    abstract fun onHandleRequestViaOkHttp(request: WebResourceRequest)
    abstract fun onInvalidHostRegex(e: Exception)
    abstract fun onFailedOKHttpRequest(request: WebResourceRequest, e: Exception)
}