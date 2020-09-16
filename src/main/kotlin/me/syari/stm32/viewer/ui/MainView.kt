package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.STLinkGDBServer
import tornadofx.View
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
        isRunning = isRunning.not()
        menuItemRun.text = if (isRunning) {
            STLinkGDBServer.launch()
            //launchArmNoneEabiGdb(Config.Plugin.GnuArmEmbedded.get()!!)
            "Stop"
        } else {
            STLinkGDBServer.cancel()
            "Run"
        }
    }
}
