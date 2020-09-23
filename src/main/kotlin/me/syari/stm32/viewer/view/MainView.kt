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
                    actionOpenElf()
                }
                menuFileOpenRecent = menu("Open Recent")
                menu("Option") {
                    item("Plugin") {
                        actionOptionPlugin()
                    }
                }
            }
            menu("Debug") {
                menuItemDebugRun = item("Run") {
                    actionDebugRun()
                }
            }
        }
    }

    private fun MenuItem.actionOpenElf() = action {
        val filterOnlyElf = arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf"))
        ArmNoneEabiGdb.elfFile = chooseFile(null, filterOnlyElf).firstOrNull()
    }

    private fun MenuItem.actionOptionPlugin() = action {
        openInternalWindow(PluginOptionView::class)
    }

    var isRunning = false

    private fun MenuItem.actionDebugRun() = action {
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
            if (stLinkGdbServerResult is STLinkGDBServer.LaunchResult.Failure) {
                return@action stLinkGdbServerResult.run()
            }
            val armNoneEabiGdbLaunchResult = ArmNoneEabiGdb.launch()
            if (armNoneEabiGdbLaunchResult is ArmNoneEabiGdb.LaunchResult.Failure) {
                return@action armNoneEabiGdbLaunchResult.run()
            }
            stLinkGdbServerResult.run()
            armNoneEabiGdbLaunchResult.run()
            "Stop"
        }
        isRunning = isRunning.not()
    }
}