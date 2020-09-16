package me.syari.stm32.viewer.ui

import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.TextField
import me.syari.stm32.viewer.Plugins
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.debug.Plugin
import tornadofx.View
import tornadofx.chooseFile
import java.io.File

class PluginOptionView : View("プラグインオプション") {
    override val root: Parent by fxml("/fxml/PluginOptionView.fxml")

    @FXML private lateinit var buttonFindCubeIDE: Button
    @FXML private lateinit var textViewCubeIDE: TextField
    @FXML private lateinit var textViewStLinkGdbServer: TextField
    @FXML private lateinit var textViewCubeProgrammer: TextField
    @FXML private lateinit var textViewGnuArmEmbedded: TextField

    init {
        updateDisplayFromConfig()
    }

    var findCubeIDETask: Task<Unit>? = null

    private fun cancelFindCubeIDETask() {
        findCubeIDETask?.let {
            it.cancel()
            findCubeIDETask = null
            buttonFindCubeIDE.isDisable = false
        }
    }

    fun clickFindCubeIDE() {
        val initialDirectory = File(textViewCubeIDE.text).parentFile
        val file = chooseFile(null, emptyArray(), initialDirectory)
        file.firstOrNull()?.let {
            textViewCubeIDE.text = it.path
            findCubeIDETask = runAsync {
                buttonFindCubeIDE.isDisable = true
                val pluginsFolder = Plugins.findPluginsFolder(it)
                val plugins = Plugins.findPlugins(pluginsFolder)
                textViewStLinkGdbServer.text = plugins?.get(Plugin.Type.StLinkGdbServer) ?: ""
                textViewCubeProgrammer.text = plugins?.get(Plugin.Type.CubeProgrammer) ?: ""
                textViewGnuArmEmbedded.text = plugins?.get(Plugin.Type.GnuArmEmbedded) ?: ""
            } ui {
                buttonFindCubeIDE.isDisable = false
                findCubeIDETask = null
            }
        }
    }

    fun clickClose() {
        cancelFindCubeIDETask()
        updateDisplayFromConfig()
        close()
    }

    fun clickSave() {
        cancelFindCubeIDETask()
        Config.saveFile {
            Config.Plugin.CubeIDE.put(textViewCubeIDE.text)
            Config.Plugin.STLinkGDBServer.put(textViewStLinkGdbServer.text)
            Config.Plugin.CubeProgrammer.put(textViewCubeProgrammer.text)
            Config.Plugin.GnuArmEmbedded.put(textViewGnuArmEmbedded.text)
        }
        close()
    }

    private fun updateDisplayFromConfig() {
        textViewCubeIDE.text = Config.Plugin.CubeIDE.get() ?: ""
        textViewStLinkGdbServer.text = Config.Plugin.STLinkGDBServer.get() ?: ""
        textViewCubeProgrammer.text = Config.Plugin.CubeProgrammer.get() ?: ""
        textViewGnuArmEmbedded.text = Config.Plugin.GnuArmEmbedded.get() ?: ""
    }
}
