package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.input.TransferMode
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.ArmNoneEabiGdb
import me.syari.stm32.viewer.debug.STLinkGDBServer
import me.syari.stm32.viewer.util.firstElfFileOrNull
import tornadofx.View
import tornadofx.chooseFile
import tornadofx.error

class MainView : View("STM32ViewerKt") {
    override val root: Parent by fxml("/fxml/MainView.fxml")

    @FXML lateinit var menuItemRun: MenuItem

    init {
        root.setOnDragOver {
            if (it.dragboard.files.firstElfFileOrNull != null) {
                it.acceptTransferModes(TransferMode.MOVE)
            }
        }
        root.setOnDragDropped {
            ArmNoneEabiGdb.elfFile = it.dragboard.files.firstElfFileOrNull
        }
    }

    @Suppress("unused") // fxml
    fun clickMenuOpenElf() {
        val filterOnlyElf = arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf"))
        ArmNoneEabiGdb.elfFile = chooseFile(null, filterOnlyElf).firstOrNull()
    }

    @Suppress("unused") // fxml
    fun clickMenuOpenPluginOption() {
        openInternalWindow(PluginOptionView::class)
    }

    var isRunning = false

    @Suppress("unused") // fxml
    fun clickMenuRun() {
        menuItemRun.text = if (isRunning) {
            ArmNoneEabiGdb.cancel()
            STLinkGDBServer.cancel()
            "Run"
        } else {
            val stLinkGdbServerResult = STLinkGDBServer.launch {
                ArmNoneEabiGdb.cancel()
                menuItemRun.text = "Run"
            }
            when (stLinkGdbServerResult) {
                is STLinkGDBServer.LaunchResult.Success -> {
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerPathIsNull -> {
                    error(
                        "ST-Link GDB Server を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerNotExits -> {
                    error(
                        "ST-Link GDB Server が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerPathIsNull -> {
                    error(
                        "CubeProgrammer を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerNotExits -> {
                    error(
                        "CubeProgrammer が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
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
                    return
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedNotExits -> {
                    error(
                        "GNU Arm Embedded が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ArmNoneEabiGdbNotExits -> {
                    error(
                        "arm-none-eabi-gdb が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileIsNull -> {
                    error(
                        ".elf ファイルを選択していません",
                        "File -> Open .elf"
                    )
                    return
                }
            }
            "Stop"
        }
        isRunning = isRunning.not()
    }
}
