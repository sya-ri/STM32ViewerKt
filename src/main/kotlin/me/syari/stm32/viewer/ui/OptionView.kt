package me.syari.stm32.viewer.ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextField
import me.syari.stm32.viewer.Plugin
import me.syari.stm32.viewer.Plugins
import tornadofx.*
import java.io.File

class OptionView: View("My View") {
    override val root: Parent by fxml("/fxml/OptionView.fxml")

    @FXML private lateinit var textViewCubeIDEPath: TextField
    @FXML private lateinit var textViewStLinkGdbServer: TextField
    @FXML private lateinit var textViewCubeProgrammer: TextField
    @FXML private lateinit var textViewArmNoneEabiGdb: TextField

    fun findCubeIDE(e: ActionEvent) {
        val initialDirectory = File(textViewCubeIDEPath.text).parentFile
        val file = chooseFile(null, emptyArray(), initialDirectory)
        file.firstOrNull()?.let {
            textViewCubeIDEPath.text = it.path
            val pluginsFolder = Plugins.findPluginsFolder(it)
            val plugins = Plugins.findPlugins(pluginsFolder)
            textViewStLinkGdbServer.text = plugins?.get(Plugin.StLinkGdbServer) ?: ""
            textViewCubeProgrammer.text = plugins?.get(Plugin.CubeProgrammer) ?: ""
            textViewArmNoneEabiGdb.text = plugins?.get(Plugin.GnuArmEmbedded) ?: ""
        }
    }
}
