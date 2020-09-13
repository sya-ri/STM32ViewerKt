package me.syari.stm32.viewer.util

import java.io.File

fun File.child(vararg child: String): File {
    var file = this
    child.forEach {
        file = File(file, it)
    }
    return file
}