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
    private const val MAX_DEPTH = 3

    fun findPluginsFolder(cube_ide_file: File): File? {
        fun findPluginsFolderRecursive(folder: File, depth: Int): File? {
            folder.listFiles()?.forEach {
                if(it.isDirectory) {
                    println("$depth ${it.path}")
                    if(it.name == "plugins") {
                        return it
                    } else if(depth != MAX_DEPTH){
                        findPluginsFolderRecursive(it, depth + 1)?.let {
                            return it
                        }
                    }
                }
            }
            return null
        }

        val firstFindDirectory = if(cube_ide_file.isDirectory) {
            cube_ide_file
        } else {
            cube_ide_file.parentFile
        }
        return findPluginsFolderRecursive(firstFindDirectory, 0)
    }

    fun findPluginsFolder(cube_ide_path: String): File? {
        return findPluginsFolder(File(cube_ide_path))
    }

    fun findPlugins(plugins_folder: File): Map<Plugin, AccessiblePlugin>? {
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
}