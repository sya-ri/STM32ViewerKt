package me.syari.stm32.viewer

fun main(){
    print("CubeIDEPath: ")
    val cubeIDEPath = readLine() ?: return println("Not Enter: CubeIDE Path")
    val pluginsFolder = Plugins.findPluginsFolder(cubeIDEPath) ?: return println("Not Found: Plugins Folder")
    val plugins = Plugins.findPlugins(pluginsFolder)
        ?: return println("Not Found: Plugins Folder")
    plugins.forEach {
        println("${it.key} ${it.value}")
    }
    //launchStLinkGdbServer(plugins)
    //launchArmNoneEabiGdb(plugins)
}

fun launchStLinkGdbServer(plugins: Map<Plugin, AccessiblePlugin>) {
    val stLinkGdbServer = plugins[Plugin.StLinkGdbServer] ?: return println("Not Found: ST-Link GDB Server")
    val cubeProgrammer = plugins[Plugin.CubeProgrammer] ?: return println("Not Found: CubeProgrammer")
    ProcessBuilder().apply {
        directory(stLinkGdbServer.toolsBin)
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(if (PlatformUtil.isWindows) {
            listOf("cmd", "/c", "ST-LINK_gdbserver.exe")
        } else {
            listOf("./ST-LINK_gdbserver")
        } + listOf("-d", "-v", "-cp", "'${cubeProgrammer.toolsBin?.absolutePath}'"))
    }.start()
}

fun launchArmNoneEabiGdb(plugins: Map<Plugin, AccessiblePlugin>) {
    val gnuArmEmbedded = plugins[Plugin.GnuArmEmbedded] ?: return println("Not Found: GnuArmEmbedded")
    ProcessBuilder().apply {
        directory(gnuArmEmbedded.toolsBin)
        redirectOutput(ProcessBuilder.Redirect.INHERIT)
        command(if (PlatformUtil.isWindows) {
            listOf("cmd", "/c", "arm-none-eabi-gdb.exe")
        } else {
            listOf("./arm-none-eabi-gdb")
        })
    }.start()
}