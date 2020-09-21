package me.syari.stm32.viewer.ui

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import me.syari.stm32.viewer.debug.ArmNoneEabiGdb
import me.syari.stm32.viewer.debug.STLinkGDBServer
import me.syari.stm32.viewer.util.enableDragDropFile
import me.syari.stm32.viewer.util.isElfFile
import tornadofx.View
import tornadofx.chooseFile
import tornadofx.error
import java.io.File

class MainView : View("STM32ViewerKt") {
    override val root: Parent by fxml("/fxml/MainView.fxml")

    @FXML lateinit var menuOpenRecent: Menu
    @FXML lateinit var menuItemRun: MenuItem

    init {
        ArmNoneEabiGdb.updateRecentElf(menuOpenRecent)
        root.enableDragDropFile(File::isElfFile) {
            ArmNoneEabiGdb.elfFile = it
        }
    }

    @Suppress("unused") // fxml
    fun clickMenuOpenElf() {
        val filterOnlyElf = arrayOf(FileChooser.ExtensionFilter("Executable File", "*.elf"))
        ArmNoneEabiGdb.elfFile = chooseFile(null, filterOnlyElf).firstOrNull()
    }

    @Suppress("unused") // fxml
    fun clickMenuOpenPluginOption() {
        //openInternalWindow(PluginOptionView::class)
        openInternalWindow(PluginOptionViewCode::class)
    }

    var isRunning = false

    @Suppress("unused") // fxml
    fun clickMenuRun() {
        println(ArmNoneEabiGdb.elfFile?.path)
        menuItemRun.text = if (isRunning) {
            ArmNoneEabiGdb.cancel()
            STLinkGDBServer.cancel()
            "Run"
        } else {
            val stLinkGdbServerResult = STLinkGDBServer.launch {
                ArmNoneEabiGdb.cancel()
                menuItemRun.text = "Run"
            }
            when (stLinkGdbServerResult) {
                is STLinkGDBServer.LaunchResult.Success -> {
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerPathIsNull -> {
                    error(
                        "ST-Link GDB Server を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.STLinkGDBServerNotExists -> {
                    error(
                        "ST-Link GDB Server が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerPathIsNull -> {
                    error(
                        "CubeProgrammer を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                STLinkGDBServer.LaunchResult.CubeProgrammerNotExists -> {
                    error(
                        "CubeProgrammer が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
            }
            when (val armNoneEabiGdbLaunchResult = ArmNoneEabiGdb.launch()) {
                is ArmNoneEabiGdb.LaunchResult.Success -> {
                    stLinkGdbServerResult.start()
                    armNoneEabiGdbLaunchResult.start()
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedPathIsNull -> {
                    error(
                        "GNU Arm Embedded を設定してください",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.GnuArmEmbeddedNotExists -> {
                    error(
                        "GNU Arm Embedded が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ArmNoneEabiGdbNotExists -> {
                    error(
                        "arm-none-eabi-gdb が見つかりませんでした",
                        "File -> Option -> Plugin"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileIsNull -> {
                    error(
                        ".elf ファイルを選択していません",
                        "File -> Open .elf"
                    )
                    return
                }
                ArmNoneEabiGdb.LaunchResult.ElfFileNotExists -> {
                    error(
                        ".elf ファイルが見つかりませんでした",
                        "File -> Open .elf"
                    )
                    return
                }
            }
            "Stop"
        }
        isRunning = isRunning.not()
    }
}
