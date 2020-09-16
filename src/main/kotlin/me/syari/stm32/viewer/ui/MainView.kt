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
        chooseFile(null, arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf")))
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
            when (STLinkGDBServer.launch()) {
                STLinkGDBServer.LaunchResult.Success -> {
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
                    alert(Alert.AlertType.ERROR, "CubeProgrammer を設定してください", "File -> Option -> Plugin", ButtonType.OK)
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
            ArmNoneEabiGdb.launch()
            "Stop"
        }
        isRunning = isRunning.not()
    }
}
