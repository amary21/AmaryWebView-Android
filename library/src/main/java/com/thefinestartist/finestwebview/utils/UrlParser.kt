package com.thefinestartist.finestwebview.utils

import java.net.MalformedURLException
import java.net.URL

object UrlParser {
  fun getHost(url: String): String {
    try {
      return URL(url).host
    } catch (e: MalformedURLException) {
      e.printStackTrace()
    }
    return url
  }
}