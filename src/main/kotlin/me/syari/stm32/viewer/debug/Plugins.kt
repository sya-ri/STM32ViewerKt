package me.syari.stm32.viewer.debug

import me.syari.stm32.viewer.util.child
import java.io.File

object Plugins {
    fun findPluginsFolder(cube_ide_file: File): File? {
        fun findPluginsFolderRecursive(folder: File): File? {
            folder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    if (file.name == "plugins") {
                        return file
                    }
                    findPluginsFolderRecursive(file)?.let {
                        return it
                    }
                }
            }
            return null
        }

        val firstFindDirectory = if (cube_ide_file.isDirectory) {
            cube_ide_file
        } else {
            cube_ide_file.parentFile
        }
        return findPluginsFolderRecursive(firstFindDirectory)
    }

    fun findPlugins(plugins_folder: File?): Map<Plugin.Type, String>? {
        if (plugins_folder == null || !plugins_folder.exists() || !plugins_folder.isDirectory) return null
        return mutableMapOf<Plugin.Type, String>().apply {
            plugins_folder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    Plugin.Type.match(file.name)?.let {
                        put(it, file.child("tools", "bin").path)
                    }
                }
            }
        }
    }
}