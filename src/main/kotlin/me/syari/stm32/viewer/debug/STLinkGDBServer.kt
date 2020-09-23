package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.*
import tornadofx.error
import tornadofx.runAsync
import java.io.File

object STLinkGDBServer {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(onExit: () -> Unit): LaunchResult {
        val stLinkGdbServerPath = Config.Plugin.STLinkGDBServer.get().nullOnEmpty
            ?: return LaunchResult.Failure.STLinkGDBServerPathIsNull
        val stLinkGdbServerFile = File(stLinkGdbServerPath).existsOrNull
            ?: return LaunchResult.Failure.STLinkGDBServerNotExists
        if (stLinkGdbServerFile.findStartsWith("ST-LINK_gdbserver").not())
            return LaunchResult.Failure.STLinkGDBServerNotExists
        val cubeProgrammerPath = Config.Plugin.CubeProgrammer.get().nullOnEmpty
            ?: return LaunchResult.Failure.CubeProgrammerPathIsNull
        val cubeProgrammerFile = File(cubeProgrammerPath).existsOrNull
            ?: return LaunchResult.Failure.CubeProgrammerNotExists
        if (cubeProgrammerFile.findStartsWith("STM32_Programmer_CLI").not())
            return LaunchResult.Failure.CubeProgrammerNotExists
        return LaunchResult.Success {
            launchTask = runAsync {
                launchProcess = ProcessBuilder().apply {
                    directory(stLinkGdbServerFile)
                    command(
                        if (PlatformUtil.isWindows) {
                            listOf("cmd", "/c", "ST-LINK_gdbserver.exe")
                        } else {
                            listOf("./ST-LINK_gdbserver")
                        } + listOf("-d", "-v", "-cp", "'$cubeProgrammerPath'")
                    )
                }.start()
                launchProcess?.waitFor()
            } finally {
                if (launchProcess?.isAlive == false) {
                    ExitErrorMessage.get(launchProcess?.exitValue())?.let { message ->
                        error(message)
                    }
                }
                launchProcess?.let {
                    it.destroy()
                    launchProcess = null
                }
                onExit()
            }
        }
    }

    sealed class LaunchResult(val run: () -> Unit) {
        class Success(start: () -> Unit) : LaunchResult(start)
        sealed class Failure(header: String, content: String) : LaunchResult({ error(header, content) }) {
            object STLinkGDBServerPathIsNull : Failure("ST-Link GDB Server を設定してください", "File -> Option -> Plugin")
            object STLinkGDBServerNotExists : Failure("ST-Link GDB Server が見つかりませんでした", "File -> Option -> Plugin")
            object CubeProgrammerPathIsNull : Failure("CubeProgrammer を設定してください", "File -> Option -> Plugin")
            object CubeProgrammerNotExists : Failure("CubeProgrammer が見つかりませんでした", "File -> Option -> Plugin")
        }
    }

    fun cancel() {
        launchTask?.let {
            it.cancel()
            launchTask = null
        }
    }

    object ExitErrorMessage {
        private val list = mapOf(
            1 to "デバイスに接続出来ませんでした",
            2 to "デバイスが見つかりませんでした",
            4 to "ST-Link がデバイスに接続されていません",
            254 to "デバイスとの接続が切れました"
        )

        fun get(exitValue: Int?) = list[exitValue]
    }
}