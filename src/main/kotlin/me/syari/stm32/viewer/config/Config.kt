package me.syari.stm32.viewer.config

import java.io.File

object Config : ConfigFile(File("stm32_viewer.properties")) {
    object Plugin {
        val CubeIDE = key("plugin.cube_ide", Key.Type.string)
        val STLinkGDBServer = key("plugin.st_link_gdb_server", Key.Type.string)
        val CubeProgrammer = key("plugin.cube_programmer", Key.Type.string)
        val GnuArmEmbedded = key("plugin.gnu_arm_embedded", Key.Type.string)
    }
}