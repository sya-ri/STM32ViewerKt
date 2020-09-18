package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.*
import tornadofx.runAsync
import java.io.File

object ArmNoneEabiGdb {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(): LaunchResult {
        val gnuArmEmbeddedPath = Config.Plugin.GnuArmEmbedded.get().nullOnEmpty
            ?: return LaunchResult.GnuArmEmbeddedPathIsNull
        val gnuArmEmbeddedFile = File(gnuArmEmbeddedPath).existsOrNull
            ?: return LaunchResult.GnuArmEmbeddedNotExits
        if (gnuArmEmbeddedFile.findStartsWith("arm-none-eabi-gdb").not())
            return LaunchResult.ArmNoneEabiGdbNotExits
        if (elfFile == null) return LaunchResult.ElfFileIsNull
        return LaunchResult.Success {
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
        }
    }

    sealed class LaunchResult {
        class Success(val start: () -> Unit) : LaunchResult()
        object GnuArmEmbeddedPathIsNull : LaunchResult()
        object GnuArmEmbeddedNotExits : LaunchResult()
        object ArmNoneEabiGdbNotExits : LaunchResult()
        object ElfFileIsNull : LaunchResult()
    }

    fun cancel() {
        launchTask?.let {
            it.cancel()
            launchTask = null
        }
    }

    var elfFile: File? = null
}