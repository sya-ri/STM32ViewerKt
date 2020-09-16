package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.PlatformUtil
import me.syari.stm32.viewer.util.finally
import tornadofx.runAsync
import java.io.File

object STLinkGDBServer {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(): LaunchResult {
        val stLinkGdbServerPath = Config.Plugin.STLinkGDBServer.get() ?: return LaunchResult.STLinkGDBServerPathIsNull
        val cubeProgrammerPath = Config.Plugin.CubeProgrammer.get() ?: return LaunchResult.CubeProgrammerPathIsNull
        launchTask = runAsync {
            launchProcess = ProcessBuilder().apply {
                directory(File(stLinkGdbServerPath))
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
                    Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show()
                }
            }
            launchProcess?.let {
                it.destroy()
                launchProcess = null
            }
        }
        return LaunchResult.Success
    }

    enum class LaunchResult {
        Success,
        STLinkGDBServerPathIsNull,
        CubeProgrammerPathIsNull
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
            2 to "デバイスが見つかりませんでした"
        )

        fun get(exitValue: Int?) = list[exitValue]
    }
}