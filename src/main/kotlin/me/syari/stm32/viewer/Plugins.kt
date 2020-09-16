package me.syari.stm32.viewer

import me.syari.stm32.viewer.util.PlatformUtil
import java.io.File

fun launchArmNoneEabiGdb(gnuArmEmbeddedPath: String) {
    ProcessBuilder().apply {
        directory(File(gnuArmEmbeddedPath))
        redirectInput(ProcessBuilder.Redirect.INHERIT)
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(
            if (PlatformUtil.isWindows) {
                listOf("cmd", "/c", "arm-none-eabi-gdb.exe")
            } else {
                listOf("./arm-none-eabi-gdb")
            }
        )
    }.start()
}