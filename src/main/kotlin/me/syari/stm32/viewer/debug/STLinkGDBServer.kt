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

    fun launch(): LaunchResult {
        val stLinkGdbServerPath = Config.Plugin.STLinkGDBServer.get()
        if (stLinkGdbServerPath.isNullOrEmpty()) return LaunchResult.STLinkGDBServerPathIsNull
        val cubeProgrammerPath = Config.Plugin.CubeProgrammer.get()
        if (cubeProgrammerPath.isNullOrEmpty()) return LaunchResult.CubeProgrammerPathIsNull
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
                    alert(Alert.AlertType.ERROR, message, null, ButtonType.OK)
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