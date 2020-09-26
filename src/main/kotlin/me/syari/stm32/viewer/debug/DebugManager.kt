package me.syari.stm32.viewer.debug

import me.syari.stm32.viewer.config.Config
import tornadofx.observableListOf
import java.io.File

object DebugManager {
    val variableTable = observableListOf<Variable>()

    var elfFile: File? = null
        set(value) {
            if (value != null) {
                field = value
                ArmNoneEabiGdb.recentElfFiles.remove(value.path)
                ArmNoneEabiGdb.recentElfFiles.add(value.path)
                Config.saveFile {
                    Config.Debug.RecentElf.put(ArmNoneEabiGdb.recentElfFiles)
                }
                ArmNoneEabiGdb.updateRecentElf()
            }
        }
}