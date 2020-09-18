package me.syari.stm32.viewer.util

val String?.nullOnEmpty
    get() = if (isNullOrEmpty()) null else this