package com.thefinestartist.finestwebview.listeners


abstract class WebViewListener {
  fun onProgressChanged(progress: Int) {}
  fun onReceivedTitle(title: String?) {}
  fun onReceivedTouchIconUrl(url: String?, precomposed: Boolean) {}
  fun onPageStarted(url: String?) {}
  fun onPageFinished(url: String?) {}
  fun onLoadResource(url: String?) {}
  fun onPageCommitVisible(url: String?) {}
  fun onDownloadStart(
    url: String?,
    userAgent: String?,
    contentDisposition: String?,
    mimeType: String?,
    contentLength: Long
  ) {
  }
}