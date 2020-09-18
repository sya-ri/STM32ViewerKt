package me.syari.stm32.viewer.debug

import me.syari.stm32.viewer.util.forEachFiles
import me.syari.stm32.viewer.util.orParentDirectory
import java.io.File

object Plugin {
    fun findPluginsFolder(cube_ide_file: File): File? {
        fun findPluginsFolderRecursive(folder: File): File? {
            folder.forEachFiles { file ->
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

        return findPluginsFolderRecursive(cube_ide_file.orParentDirectory)
    }

    fun findPlugins(plugins_folder: File?): Map<Type, String>? {
        if (plugins_folder == null || plugins_folder.exists().not() || plugins_folder.isDirectory.not()) return null
        return mutableMapOf<Type, String>().apply {
            plugins_folder.forEachFiles { file ->
                if (file.isDirectory) {
                    Type.match(file.name)?.let {
                        put(it, file.resolve("tools").resolve("bin").path)
                    }
                }
            }
        }
    }

    enum class Type(
        val directoryName: String,
    ) {
        CubeProgrammer("com.st.stm32cube.ide.mcu.externaltools.cubeprogrammer"),
        StLinkGdbServer("com.st.stm32cube.ide.mcu.externaltools.stlink-gdb-server"),
        GnuArmEmbedded("com.st.stm32cube.ide.mcu.externaltools.gnu-arm-embedded");

        companion object {
            fun match(directoryName: String): Type? {
                return values().firstOrNull { directoryName.startsWith(it.directoryName) }
            }
        }
    }
}