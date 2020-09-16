package me.syari.stm32.viewer

import me.syari.stm32.viewer.debug.Plugin
import me.syari.stm32.viewer.util.PlatformUtil
import me.syari.stm32.viewer.util.child
import java.io.File

object Plugins {
    fun findPluginsFolder(cube_ide_file: File): File? {
        fun findPluginsFolderRecursive(
            folder: File,
            depth: Int,
        ): File? {
            folder.listFiles()?.forEach {
                if (it.isDirectory) {
                    if (it.name == "plugins") {
                        return it
                    }
                    findPluginsFolderRecursive(it, depth + 1)?.let {
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
        return findPluginsFolderRecursive(firstFindDirectory, 0)
    }

    fun findPlugins(plugins_folder: File?): Map<Plugin.Type, String>? {
        if (plugins_folder == null || !plugins_folder.exists() || !plugins_folder.isDirectory) return null
        return mutableMapOf<Plugin.Type, String>().apply {
            plugins_folder.listFiles()?.forEach { file ->
                if (!file.isDirectory) return@forEach
                Plugin.Type.match(file.name)?.let {
                    put(it, file.child("tools", "bin").path)
                }
            }
        }
    }
}

fun launchStLinkGdbServer(
    stLinkGdbServerPath: String,
    cubeProgrammerPath: String,
) {
    ProcessBuilder().apply {
        directory(File(stLinkGdbServerPath))
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(
            if (PlatformUtil.isWindows) {
                listOf("cmd", "/c", "ST-LINK_gdbserver.exe")
            } else {
                listOf("./ST-LINK_gdbserver")
            } + listOf("-d", "-v", "-cp", "'$cubeProgrammerPath'")
        )
    }.start()
}

fun launchArmNoneEabiGdb(gnuArmEmbeddedPath: String) {
    ProcessBuilder().apply {
        directory(File(gnuArmEmbeddedPath))
        redirectInput(ProcessBuilder.Redirect.INHERIT)
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(
            if (PlatformUtil.isWindows) {
                listOf("cmd", "/c", "arm-none-eabi-gdb.exe")
            } else {
                listOf("./arm-none-eabi-gdb")
            }
        )
    }.start()
}