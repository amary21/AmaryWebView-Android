package com.thefinestartist.finestwebview.utils

fun String?.orEmpty(): String {
    return this ?: ""
}

fun Int?.orEmpty(): Int {
    return this ?: 0
}

fun Boolean?.orEmpty(): Boolean {
    return this ?: false
}