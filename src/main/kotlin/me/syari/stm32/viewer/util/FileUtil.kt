package me.syari.stm32.viewer.util

import java.io.File

val File.existsOrNull
    get() = if (exists()) this else null

fun fileOrNull(pathname: String?) = pathname?.let { File(it) }