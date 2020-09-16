package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.PlatformUtil
import me.syari.stm32.viewer.util.finally
import tornadofx.runAsync
import java.io.File

object ArmNoneEabiGdb {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(): LaunchResult {
        val gnuArmEmbeddedPath = Config.Plugin.GnuArmEmbedded.get()
        if (gnuArmEmbeddedPath.isNullOrEmpty()) return LaunchResult.GnuArmEmbeddedPathIsNull
        val gnuArmEmbeddedFile = File(gnuArmEmbeddedPath)
        if (!gnuArmEmbeddedFile.exists()) return LaunchResult.GnuArmEmbeddedNotExits
        if (gnuArmEmbeddedFile.list()?.firstOrNull { it.startsWith("arm-none-eabi-gdb") } == null)
            return LaunchResult.ArmNoneEabiGdbNotExits
        launchTask = runAsync {
            launchProcess = ProcessBuilder().apply {
                directory(gnuArmEmbeddedFile)
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
            launchProcess?.waitFor()
        } finally {
            launchProcess?.let {
                it.destroy()
                launchProcess = null
            }
        }
        return LaunchResult.Success
    }

    enum class LaunchResult {
        Success,
        GnuArmEmbeddedPathIsNull,
        GnuArmEmbeddedNotExits,
        ArmNoneEabiGdbNotExits
    }

    fun cancel() {
        launchTask?.let {
            it.cancel()
            launchTask = null
        }
    }
}