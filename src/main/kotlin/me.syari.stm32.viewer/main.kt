package me.syari.stm32.viewer

fun main(){
    val plugins = Plugins.find("/Applications/STM32CubeIDE.app/Contents/Eclipse/plugins")
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
        command("./ST-LINK_gdbserver", "-d", "-v", "-cp", "'${cubeProgrammer.toolsBin?.absolutePath}'")
    }.start()
}