package me.syari.stm32.viewer

object PlatformUtil {
    private val OS_NAME = System.getProperty("os.name").toLowerCase()

    val isWindows
        get() = OS_NAME.startsWith("windows")

    val isMac
        get() = OS_NAME.startsWith("mac")

    val isLinux
        get() = OS_NAME.startsWith("linux")
}