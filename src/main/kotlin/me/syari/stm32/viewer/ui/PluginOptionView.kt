package me.syari.stm32.viewer.ui

import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.TextField
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.debug.Plugin
import me.syari.stm32.viewer.util.finally
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

    private var findCubeIDETask: Task<Unit>? = null

    @Suppress("unused") // fxml
    fun clickFindCubeIDE() {
        var initialDirectory = File(textViewCubeIDE.text).parentFile
        if (initialDirectory.exists().not()) initialDirectory = null
        val file = chooseFile(null, emptyArray(), initialDirectory)
        file.firstOrNull()?.let {
            textViewCubeIDE.text = it.path
            findCubeIDETask = runAsync {
                buttonFindCubeIDE.isDisable = true
                val pluginsFolder = Plugin.findPluginsFolder(it)
                val plugins = Plugin.findPlugins(pluginsFolder)
                textViewStLinkGdbServer.text = plugins?.get(Plugin.Type.StLinkGdbServer) ?: ""
                textViewCubeProgrammer.text = plugins?.get(Plugin.Type.CubeProgrammer) ?: ""
                textViewGnuArmEmbedded.text = plugins?.get(Plugin.Type.GnuArmEmbedded) ?: ""
            } finally {
                buttonFindCubeIDE.isDisable = false
                findCubeIDETask = null
            }
        }
    }

    @Suppress("unused") // fxml
    fun clickClose() {
        findCubeIDETask?.cancel()
        updateDisplayFromConfig()
        close()
    }

    @Suppress("unused") // fxml
    fun clickSave() {
        findCubeIDETask?.cancel()
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
