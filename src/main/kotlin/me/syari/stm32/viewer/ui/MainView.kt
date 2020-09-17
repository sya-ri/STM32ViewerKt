package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.ArmNoneEabiGdb
import me.syari.stm32.viewer.debug.STLinkGDBServer
import tornadofx.View
import tornadofx.alert
import tornadofx.chooseFile

class MainView : View("STM32ViewerKt") {
    override val root: Parent by fxml("/fxml/MainView.fxml")

    @FXML lateinit var menuItemRun: MenuItem

    @Suppress("unused") // fxml
    fun clickMenuOpenElf() {
        val files = chooseFile(null, arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf")))
        files.firstOrNull()?.let {
            ArmNoneEabiGdb.elfFile = it
        }
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
                    alert(
                        Alert.AlertType.ERROR,
                        "ST-Link GDB Server を設定してください",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerNotExits -> {
                    alert(
                        Alert.AlertType.ERROR,
                        "ST-Link GDB Server が見つかりませんでした",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerPathIsNull -> {
                    alert(
                        Alert.AlertType.ERROR,
                        "CubeProgrammer を設定してください",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerNotExits -> {
                    alert(
                        Alert.AlertType.ERROR,
                        "CubeProgrammer が見つかりませんでした",
                        "File -> Option -> Plugin",
                        ButtonType.OK
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
                    alert(
                        Alert.AlertType.ERROR,
                        "GNU Arm Embedded を設定してください",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedNotExits -> {
                    alert(
                        Alert.AlertType.ERROR,
                        "GNU Arm Embedded が見つかりませんでした",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ArmNoneEabiGdbNotExits -> {
                    alert(
                        Alert.AlertType.ERROR,
                        "arm-none-eabi-gdb が見つかりませんでした",
                        "File -> Option -> Plugin",
                        ButtonType.OK
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileIsNull -> {
                    alert(
                        Alert.AlertType.ERROR,
                        ".elf ファイルを選択していません",
                        "File -> Open .elf",
                        ButtonType.OK
                    )
                    return
                }
            }
            "Stop"
        }
        isRunning = isRunning.not()
    }
}
