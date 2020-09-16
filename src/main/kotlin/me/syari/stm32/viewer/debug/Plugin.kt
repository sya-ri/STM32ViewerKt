package me.syari.stm32.viewer.debug

import java.io.File

object Plugin {
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

    data class Data(val type: Type, val directory: File?) {
        val path
            get() = directory?.path ?: ""

        companion object {
            fun fromPath(type: Type, path: String?): Data {
                return Data(type, path?.let { File(it) })
            }
        }
    }
}