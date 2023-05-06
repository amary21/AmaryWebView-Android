package com.amary.amarywebview.utils

import com.amary.amarywebview.helpers.ChuckerListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun String.getHostRegex(listener: ChuckerListener? = null): Regex {
    return try {
        var str = this
        if (str.startsWith("*")) {
            str = str.replace("*", "(.*.)?")
        }
        str.toRegex()
    } catch (e: Exception) {
        listener?.onInvalidHostRegex(e)
        "FAILED_REGEX".toRegex()
    }
}

fun String?.isJson(): Boolean {
    if (this.isNullOrBlank()) return false
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            return false
        }
    }
    return true
}