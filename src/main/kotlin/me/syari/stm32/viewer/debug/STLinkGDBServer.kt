package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.PlatformUtil
import me.syari.stm32.viewer.util.finally
import tornadofx.alert
import tornadofx.runAsync
import java.io.File

object STLinkGDBServer {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(onExit: () -> Unit): LaunchResult {
        val stLinkGdbServerPath = Config.Plugin.STLinkGDBServer.get()
        if (stLinkGdbServerPath.isNullOrEmpty()) return LaunchResult.STLinkGDBServerPathIsNull
        val stLinkGdbServerFile = File(stLinkGdbServerPath)
        if (stLinkGdbServerFile.exists().not()) return LaunchResult.STLinkGDBServerNotExits
        if (stLinkGdbServerFile.list()?.firstOrNull { it.startsWith("ST-LINK_gdbserver") } == null)
            return LaunchResult.STLinkGDBServerNotExits
        val cubeProgrammerPath = Config.Plugin.CubeProgrammer.get()
        if (cubeProgrammerPath.isNullOrEmpty()) return LaunchResult.CubeProgrammerPathIsNull
        val cubeProgrammerFile = File(cubeProgrammerPath)
        if (cubeProgrammerFile.exists().not()) return LaunchResult.CubeProgrammerNotExits
        if (cubeProgrammerFile.list()?.firstOrNull { it.startsWith("STM32_Programmer_CLI") } == null)
            return LaunchResult.CubeProgrammerNotExits
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
                        alert(Alert.AlertType.ERROR, message, null, ButtonType.OK)
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

    sealed class LaunchResult {
        class Success(val start: () -> Unit) : LaunchResult()
        object STLinkGDBServerPathIsNull : LaunchResult()
        object STLinkGDBServerNotExits : LaunchResult()
        object CubeProgrammerPathIsNull : LaunchResult()
        object CubeProgrammerNotExits : LaunchResult()
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