package me.syari.stm32.viewer.view

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.ArmNoneEabiGdb
import me.syari.stm32.viewer.debug.STLinkGDBServer
import me.syari.stm32.viewer.util.enableDragDropFile
import me.syari.stm32.viewer.util.isElfFile
import tornadofx.*
import java.io.File

class MainView : View("STM32ViewerKt") {
    lateinit var menuFileOpenRecent: Menu
    lateinit var menuItemDebugRun: MenuItem

    override fun onDock() {
        ArmNoneEabiGdb.updateRecentElf(menuFileOpenRecent)
        root.enableDragDropFile(File::isElfFile) {
            ArmNoneEabiGdb.elfFile = it
        }
    }

    override val root = vbox {
        prefHeight = 600.0
        prefWidth = 900.0

        menubar {
            menu("File") {
                item("Open .elf") {
                    action(menuItemOpenElfAction)
                }
                menuFileOpenRecent = menu("Open Recent")
                menu("Option") {
                    item("Plugin") {
                        action(menuItemOptionPluginAction)
                    }
                }
            }
            menu("Debug") {
                menuItemDebugRun = item("Run") {
                    debugRunAction()
                }
            }
        }
    }

    private inline val menuItemOpenElfAction: () -> Unit
        get() = {
            val filterOnlyElf = arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf"))
            ArmNoneEabiGdb.elfFile = chooseFile(null, filterOnlyElf).firstOrNull()
        }

    private inline val menuItemOptionPluginAction: () -> Unit
        get() = {
            openInternalWindow(PluginOptionView::class)
        }

    var isRunning = false

    private fun MenuItem.debugRunAction() = action {
        println(ArmNoneEabiGdb.elfFile?.path)
        menuItemDebugRun.text = if (isRunning) {
            ArmNoneEabiGdb.cancel()
            STLinkGDBServer.cancel()
            "Run"
        } else {
            val stLinkGdbServerResult = STLinkGDBServer.launch {
                ArmNoneEabiGdb.cancel()
                menuItemDebugRun.text = "Run"
            }
            when (stLinkGdbServerResult) {
                is STLinkGDBServer.LaunchResult.Success -> {
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerPathIsNull -> {
                    error(
                        "ST-Link GDB Server を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerNotExists -> {
                    error(
                        "ST-Link GDB Server が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerPathIsNull -> {
                    error(
                        "CubeProgrammer を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerNotExists -> {
                    error(
                        "CubeProgrammer が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
            }
            when (val armNoneEabiGdbLaunchResult = ArmNoneEabiGdb.launch()) {
                is ArmNoneEabiGdb.LaunchResult.Success -> {
                    stLinkGdbServerResult.start()
                    armNoneEabiGdbLaunchResult.start()
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedPathIsNull -> {
                    error(
                        "GNU Arm Embedded を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedNotExists -> {
                    error(
                        "GNU Arm Embedded が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                ArmNoneEabiGdb.LaunchResult.ArmNoneEabiGdbNotExists -> {
                    error(
                        "arm-none-eabi-gdb が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return@action
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileIsNull -> {
                    error(
                        ".elf ファイルを選択していません",
                        "File -> Open .elf"
                    )
                    return@action
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileNotExists -> {
                    error(
                        ".elf ファイルが見つかりませんでした",
                        "File -> Open .elf"
                    )
                    return@action
                }
            }
            "Stop"
        }
        isRunning = isRunning.not()
    }
}