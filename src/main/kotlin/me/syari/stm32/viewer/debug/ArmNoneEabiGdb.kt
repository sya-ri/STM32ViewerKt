package me.syari.stm32.viewer.debug

import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.util.*
import tornadofx.action
import tornadofx.error
import tornadofx.runAsync
import java.io.File

object ArmNoneEabiGdb {
    private var launchProcess: Process? = null
    private var launchTask: Task<Int?>? = null

    fun launch(): LaunchResult {
        val gnuArmEmbeddedPath = Config.Plugin.GnuArmEmbedded.get().nullOnEmpty
            ?: return LaunchResult.Failure.GnuArmEmbeddedPathIsNull
        val gnuArmEmbeddedFile = File(gnuArmEmbeddedPath).existsOrNull
            ?: return LaunchResult.Failure.GnuArmEmbeddedNotExists
        if (gnuArmEmbeddedFile.findStartsWith("arm-none-eabi-gdb").not())
            return LaunchResult.Failure.ArmNoneEabiGdbNotExists
        if (elfFile == null) return LaunchResult.Failure.ElfFileIsNull
        if (elfFile?.exists() != false) return LaunchResult.Failure.ElfFileNotExists
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

    sealed class LaunchResult(val run: () -> Unit) {
        class Success(run: () -> Unit) : LaunchResult(run)
        sealed class Failure(header: String, content: String) : LaunchResult({ error(header, content) }) {
            object GnuArmEmbeddedPathIsNull : Failure("GNU Arm Embedded を設定してください", "File -> Option -> Plugin")
            object GnuArmEmbeddedNotExists : Failure("GNU Arm Embedded が見つかりませんでした", "File -> Option -> Plugin")
            object ArmNoneEabiGdbNotExists : Failure("arm-none-eabi-gdb が見つかりませんでした", "File -> Option -> Plugin")
            object ElfFileIsNull : Failure(".elf ファイルを選択していません", "File -> Open .elf")
            object ElfFileNotExists : Failure(".elf ファイルが見つかりませんでした", "File -> Open .elf")
        }
    }

    fun cancel() {
        launchTask?.let {
            it.cancel()
            launchTask = null
        }
    }

    var elfFile: File? = null
        set(value) {
            if (value != null) {
                field = value
                recentElfFiles.remove(value.path)
                recentElfFiles.add(value.path)
                Config.saveFile {
                    Config.Debug.RecentElf.put(recentElfFiles)
                }
                updateRecentElf()
            }
        }

    private val recentElfFiles = Config.Debug.RecentElf.get()?.filter(String::isNotBlank).toMutableListOrEmpty()

    private var recentElfFileMenu: Menu? = null

    fun updateRecentElf(hookMenu: Menu? = null) {
        if (hookMenu != null) recentElfFileMenu = hookMenu
        recentElfFileMenu?.let { menu ->
            menu.items.clear()
            recentElfFiles.forEach {
                menu.items.add(MenuItem(it).apply {
                    action {
                        elfFile = File(it)
                    }
                })
            }
            if (recentElfFiles.isNotEmpty()) {
                menu.items.add(SeparatorMenuItem())
            }
            menu.items.add(MenuItem("Clear List").apply {
                action {
                    recentElfFiles.clear()
                    Config.saveFile {
                        Config.Debug.RecentElf.put(recentElfFiles)
                    }
                    updateRecentElf()
                }
            })
        }
    }
}