package me.syari.stm32.viewer.ui

import javafx.concurrent.Task
import javafx.scene.control.Button
import javafx.scene.control.TextField
import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.debug.Plugin
import me.syari.stm32.viewer.util.existsOrNull
import me.syari.stm32.viewer.util.fileOrNull
import me.syari.stm32.viewer.util.finally
import tornadofx.*

class PluginOptionViewCode : View("プラグイン オプション") {
    private lateinit var buttonFindCubeIDE: Button
    private lateinit var textFieldCubeIDE: TextField
    private lateinit var textFieldStLinkGdbServer: TextField
    private lateinit var textFieldCubeProgrammer: TextField
    private lateinit var textFieldGnuArmEmbedded: TextField

    override fun onDock() {
        updateDisplayFromConfig()
    }

    override val root = vbox {
        prefHeight = 250.0
        prefWidth = 600.0

        hbox {
            label("CubeIDE") {
                prefHeight = 30.0
                prefWidth = 80.0
                hboxConstraints {
                    marginLeft = 20.0
                }
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
                    marginLeftRight(20.0)
                }
                action(buttonFindCubeIDEAction)
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("ST-Link GDB Server") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textFieldStLinkGdbServer = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("CubeProgrammer") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textFieldCubeProgrammer = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("GNU Arm Embedded") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textFieldGnuArmEmbedded = textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            button("キャンセル") {
                prefHeight = 30.0
                prefWidth = 100.0
                hboxConstraints {
                    marginLeft = 150.0
                    marginRight = 50.0
                }
                action(buttonCancelAction)
            }
            button("保存") {
                prefHeight = 30.0
                prefWidth = 100.0
                hboxConstraints {
                    marginLeft = 50.0
                    marginRight = 150.0
                }
                action(buttonSaveAction)
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }
    }

    private var findCubeIDETask: Task<Unit>? = null

    private inline val buttonFindCubeIDEAction
        get(): () -> Unit = {
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

    private inline val buttonCancelAction
        get() = {
            findCubeIDETask?.cancel()
            updateDisplayFromConfig()
            close()
        }

    private inline val buttonSaveAction
        get() = {
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
