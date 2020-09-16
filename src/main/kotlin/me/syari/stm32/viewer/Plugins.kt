package me.syari.stm32.viewer

import me.syari.stm32.viewer.util.PlatformUtil
import java.io.File

fun launchStLinkGdbServer(
    stLinkGdbServerPath: String,
    cubeProgrammerPath: String,
) {
    ProcessBuilder().apply {
        directory(File(stLinkGdbServerPath))
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(
            if (PlatformUtil.isWindows) {
                listOf("cmd", "/c", "ST-LINK_gdbserver.exe")
            } else {
                listOf("./ST-LINK_gdbserver")
            } + listOf("-d", "-v", "-cp", "'$cubeProgrammerPath'")
        )
    }.start()
}

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