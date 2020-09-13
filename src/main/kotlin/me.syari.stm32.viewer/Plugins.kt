package me.syari.stm32.viewer

import java.io.File

data class AccessiblePlugin (
    val plugin: Plugin,
    val folder: File
) {
    val toolsBin: File?

    init {
        val toolsBin = File(File(folder, "tools"), "bin")
        this.toolsBin = if (toolsBin.exists() && toolsBin.isDirectory) {
            toolsBin
        } else {
            null
        }
    }
}

enum class Plugin(
    val folder_name: String
) {
    CubeProgrammer("com.st.stm32cube.ide.mcu.externaltools.cubeprogrammer"),
    StLinkGdbServer("com.st.stm32cube.ide.mcu.externaltools.stlink-gdb-server"),
    GnuArmEmbedded("com.st.stm32cube.ide.mcu.externaltools.gnu-arm-embedded");

    companion object {
        fun match(folder_name: String): Plugin? {
            return values().firstOrNull { folder_name.startsWith(it.folder_name) }
        }
    }
}

object Plugins {
    fun find(plugins_folder: File): Map<Plugin, AccessiblePlugin>? {
        if (!plugins_folder.exists() || !plugins_folder.isDirectory) return null
        return mutableMapOf<Plugin, AccessiblePlugin>().apply {
            plugins_folder.listFiles()?.forEach { file ->
                if (!file.isDirectory) return@forEach
                Plugin.match(file.name)?.let {
                    put(it, AccessiblePlugin(it, file))
                }
            }
        }
    }

    fun find(plugins_folder_path: String): Map<Plugin, AccessiblePlugin>? {
        return find(File(plugins_folder_path))
    }
}