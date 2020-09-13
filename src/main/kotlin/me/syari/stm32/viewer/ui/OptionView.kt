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
            Plugins.findPluginsFolder(it)?.let { pluginsFolder ->
                Plugins.findPlugins(pluginsFolder)?.let { plugins ->
                    plugins[Plugin.StLinkGdbServer]?.let { plugin ->
                        textViewStLinkGdbServer.text = plugin.folder.path
                    }
                    plugins[Plugin.CubeProgrammer]?.let { plugin ->
                        textViewCubeProgrammer.text = plugin.folder.path
                    }
                    plugins[Plugin.GnuArmEmbedded]?.let { plugin ->
                        textViewArmNoneEabiGdb.text = plugin.folder.path
                    }
                }
            }
        }
    }
}
