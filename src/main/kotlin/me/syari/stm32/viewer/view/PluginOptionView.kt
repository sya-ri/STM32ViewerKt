package me.syari.stm32.viewer.view

import javafx.concurrent.Task
import javafx.scene.control.Button
import javafx.scene.control.TextField
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.debug.Plugin
import me.syari.stm32.viewer.util.existsOrNull
import me.syari.stm32.viewer.util.fileOrNull
import me.syari.stm32.viewer.util.finally
import tornadofx.*

class PluginOptionView : View("プラグイン オプション") {
    private lateinit var buttonFindCubeIDE: Button
    private lateinit var textFieldCubeIDE: TextField
    private lateinit var textFieldStLinkGdbServer: TextField
    private lateinit var textFieldCubeProgrammer: TextField
    private lateinit var textFieldGnuArmEmbedded: TextField

    override fun onDock() {
        updateDisplayFromConfig()
    }

    override val root = vbox(20.0) {
        prefHeight = 250.0
        prefWidth = 600.0
        paddingTop = 10.0
        paddingLeft = 20.0

        hbox {
            label("CubeIDE") {
                prefHeight = 30.0
                prefWidth = 80.0
            }
            textFieldCubeIDE = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            buttonFindCubeIDE = button("開く") {
                prefHeight = 30.0
                prefWidth = 50.0
                hboxConstraints {
                    marginLeft = 15.0
                }
                action(buttonFindCubeIDEAction)
            }
        }

        hbox {
            label("ST-Link GDB Server") {
                prefHeight = 30.0
                prefWidth = 150.0
            }
            textFieldStLinkGdbServer = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeft = 5.0
                }
            }
        }

        hbox {
            label("CubeProgrammer") {
                prefHeight = 30.0
                prefWidth = 150.0
            }
            textFieldCubeProgrammer = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeft = 5.0
                }
            }
        }

        hbox {
            label("GNU Arm Embedded") {
                prefHeight = 30.0
                prefWidth = 150.0
            }
            textFieldGnuArmEmbedded = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeft = 5.0
                }
            }
        }

        hbox {
            button("キャンセル") {
                prefHeight = 30.0
                prefWidth = 100.0
                hboxConstraints {
                    marginLeft = 130.0
                    marginRight = 100.0
                }
                actionCancel()
            }
            button("保存") {
                prefHeight = 30.0
                prefWidth = 100.0
                actionSave()
            }
        }
    }

    private var findCubeIDETask: Task<Unit>? = null

    private inline val buttonFindCubeIDEAction: () -> Unit
        get() = {
            val initialDirectory = fileOrNull(textFieldCubeIDE.text)?.parentFile?.existsOrNull
            val file = chooseFile(null, emptyArray(), initialDirectory).firstOrNull()
            file?.let {
                textFieldCubeIDE.text = it.path
                findCubeIDETask = runAsync {
                    buttonFindCubeIDE.isDisable = true
                    val pluginsFolder = Plugin.findPluginsFolder(it)
                    val plugins = Plugin.findPlugins(pluginsFolder)
                    textFieldStLinkGdbServer.text = plugins?.get(Plugin.Type.StLinkGdbServer) ?: ""
                    textFieldCubeProgrammer.text = plugins?.get(Plugin.Type.CubeProgrammer) ?: ""
                    textFieldGnuArmEmbedded.text = plugins?.get(Plugin.Type.GnuArmEmbedded) ?: ""
                } finally {
                    buttonFindCubeIDE.isDisable = false
                    findCubeIDETask = null
                }
            }
        }

    private fun Button.actionCancel() = action {
        findCubeIDETask?.cancel()
        updateDisplayFromConfig()
        close()
    }

    private fun Button.actionSave() = action {
        findCubeIDETask?.cancel()
        Config.saveFile {
            Config.Plugin.CubeIDE.put(textFieldCubeIDE.text)
            Config.Plugin.STLinkGDBServer.put(textFieldStLinkGdbServer.text)
            Config.Plugin.CubeProgrammer.put(textFieldCubeProgrammer.text)
            Config.Plugin.GnuArmEmbedded.put(textFieldGnuArmEmbedded.text)
        }
        close()
    }

    private fun updateDisplayFromConfig() {
        textFieldCubeIDE.text = Config.Plugin.CubeIDE.get() ?: ""
        textFieldStLinkGdbServer.text = Config.Plugin.STLinkGDBServer.get() ?: ""
        textFieldCubeProgrammer.text = Config.Plugin.CubeProgrammer.get() ?: ""
        textFieldGnuArmEmbedded.text = Config.Plugin.GnuArmEmbedded.get() ?: ""
    }
}
