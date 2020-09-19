package me.syari.stm32.viewer.util

fun <T> List<T>?.toMutableListOrEmpty() = this?.toMutableList() ?: mutableListOf()