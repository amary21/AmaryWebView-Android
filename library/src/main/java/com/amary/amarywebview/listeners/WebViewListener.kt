package com.amary.amarywebview.listeners

abstract class WebViewListener {
  fun onProgressChanged(progress: Int) {}
  open fun onReceivedTitle(title: String?) {}
  open fun onReceivedTouchIconUrl(url: String?, preComposed: Boolean) {}
  open fun onPageStarted(url: String?) {}
  open fun onPageFinished(url: String?) {}
  open fun onLoadResource(url: String?) {}
  open fun onPageCommitVisible(url: String?) {}
  open fun onDownloadStart(
    url: String?,
    userAgent: String?,
    contentDisposition: String?,
    mimeType: String?,
    contentLength: Long
  ) {
  }
}