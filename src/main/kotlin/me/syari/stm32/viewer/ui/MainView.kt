package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import tornadofx.View
import tornadofx.chooseFile

class MainView : View("STM32ViewerKt") {
    override val root: Parent by fxml("/fxml/MainView.fxml")

    @FXML lateinit var menuItemRun: MenuItem

    fun clickMenuOpenElf() {
        chooseFile(null, arrayOf(FileChooser.ExtensionFilter("Executable File", "elf")))
    }

    fun clickMenuOpenPluginConfig() {
        openInternalWindow(PluginOptionView::class)
    }

    var isRunning = false

    fun clickMenuRun() {
        isRunning = isRunning.not()
        menuItemRun.text = if (isRunning) {
            "Stop"
        } else {
            "Run"
        }
    }
}
