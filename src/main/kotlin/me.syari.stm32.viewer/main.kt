package me.syari.stm32.viewer

fun main(){
    print("PluginsFolderPath: ")
    val pluginsFolderPath = readLine() ?: return println("Not Enter: Plugins Folder Path")
    val plugins = Plugins.find(pluginsFolderPath)
        ?: return println("Not Found: Plugins Folder")
    plugins.forEach {
        println("${it.key} ${it.value}")
    }
    launchStLinkGdbServer(plugins)
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
            listOf("sh", "-c", "./ST-LINK_gdbserver")
        } + listOf("-d", "-v", "-cp", "'${cubeProgrammer.toolsBin?.absolutePath}'"))
    }.start()
}