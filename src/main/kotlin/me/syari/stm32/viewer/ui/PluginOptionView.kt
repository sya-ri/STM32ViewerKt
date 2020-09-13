package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextField
import me.syari.stm32.viewer.Plugin
import me.syari.stm32.viewer.Plugins
import me.syari.stm32.viewer.config.Config
import tornadofx.View
import tornadofx.chooseFile
import java.io.File

class PluginOptionView : View("プラグインオプション") {
    override val root: Parent by fxml("/fxml/PluginOptionView.fxml")

    @FXML private lateinit var textViewCubeIDE: TextField
    @FXML private lateinit var textViewStLinkGdbServer: TextField
    @FXML private lateinit var textViewCubeProgrammer: TextField
    @FXML private lateinit var textViewGnuArmEmbedded: TextField

    init {
        textViewCubeIDE.text = Config.Plugin.CubeIDE.get() ?: ""
        textViewStLinkGdbServer.text = Config.Plugin.STLinkGDBServer.get() ?: ""
        textViewCubeProgrammer.text = Config.Plugin.CubeProgrammer.get() ?: ""
        textViewGnuArmEmbedded.text = Config.Plugin.GnuArmEmbedded.get() ?: ""
    }

    fun findCubeIDE() {
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

    fun clickClose() {
        close()
    }

    fun clickSave() {
        Config.saveFile {
            Config.Plugin.CubeIDE.put(textViewCubeIDE.text)
            Config.Plugin.STLinkGDBServer.put(textViewStLinkGdbServer.text)
            Config.Plugin.CubeProgrammer.put(textViewCubeProgrammer.text)
            Config.Plugin.GnuArmEmbedded.put(textViewGnuArmEmbedded.text)
        }
    }
}
