package me.syari.stm32.viewer.view

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.ArmNoneEabiGdb
import me.syari.stm32.viewer.debug.DebugManager
import me.syari.stm32.viewer.debug.STLinkGDBServer
import me.syari.stm32.viewer.debug.Variable
import me.syari.stm32.viewer.util.enableDragDropFile
import me.syari.stm32.viewer.util.isElfFile
import tornadofx.*
import java.io.File

class MainView : View("STM32ViewerKt") {
    private lateinit var menuFileOpenRecent: Menu
    private lateinit var menuItemDebugRun: MenuItem

    override fun onDock() {
        ArmNoneEabiGdb.updateRecentElf(menuFileOpenRecent)
        root.enableDragDropFile(File::isElfFile) {
            DebugManager.elfFile = it
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

        tableview(DebugManager.variableTable) {
            readonlyColumn("Name", Variable::name)
            readonlyColumn("Type", Variable::typeName)
            readonlyColumn("Value", Variable::value)

            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        }
    }

    private fun MenuItem.actionOpenElf() = action {
        val filterOnlyElf = arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf"))
        DebugManager.elfFile = chooseFile(null, filterOnlyElf).firstOrNull()
    }

    private fun MenuItem.actionOptionPlugin() = action {
        openInternalWindow(PluginOptionView::class)
    }

    private var isRunning = false

    private fun MenuItem.actionDebugRun() = action {
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