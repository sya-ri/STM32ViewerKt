package me.syari.stm32.viewer.ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextField
import me.syari.stm32.viewer.Plugin
import me.syari.stm32.viewer.Plugins
import me.syari.stm32.viewer.config.Properties
import tornadofx.View
import tornadofx.chooseFile
import java.io.File

class OptionView : View("My View") {
    override val root: Parent by fxml("/fxml/OptionView.fxml")

    @FXML private lateinit var textViewCubeIDE: TextField
    @FXML private lateinit var textViewStLinkGdbServer: TextField
    @FXML private lateinit var textViewCubeProgrammer: TextField
    @FXML private lateinit var textViewGnuArmEmbedded: TextField

    init {
        Properties.load() // TODO 起動時に読みこむように修正
        textViewCubeIDE.text = Properties.Plugin.CubeIDE.get() ?: ""
        textViewStLinkGdbServer.text = Properties.Plugin.STLinkGDBServer.get() ?: ""
        textViewCubeProgrammer.text = Properties.Plugin.CubeProgrammer.get() ?: ""
        textViewGnuArmEmbedded.text = Properties.Plugin.GnuArmEmbedded.get() ?: ""
    }

    fun findCubeIDE(e: ActionEvent) {
        val initialDirectory = File(textViewCubeIDE.text).parentFile
        val file = chooseFile(null, emptyArray(), initialDirectory)
        file.firstOrNull()?.let {
            textViewCubeIDE.text = it.path
            val pluginsFolder = Plugins.findPluginsFolder(it)
            val plugins = Plugins.findPlugins(pluginsFolder)
            textViewStLinkGdbServer.text = plugins?.get(Plugin.StLinkGdbServer) ?: ""
            textViewCubeProgrammer.text = plugins?.get(Plugin.CubeProgrammer) ?: ""
            textViewGnuArmEmbedded.text = plugins?.get(Plugin.GnuArmEmbedded) ?: ""
        }
    }

    fun save(e: ActionEvent) {
        Properties.saveFile {
            Properties.Plugin.CubeIDE.put(textViewCubeIDE.text)
            Properties.Plugin.STLinkGDBServer.put(textViewStLinkGdbServer.text)
            Properties.Plugin.CubeProgrammer.put(textViewCubeProgrammer.text)
            Properties.Plugin.GnuArmEmbedded.put(textViewGnuArmEmbedded.text)
        }
    }
}
