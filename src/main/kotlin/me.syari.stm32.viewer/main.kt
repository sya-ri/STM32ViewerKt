package me.syari.stm32.viewer

@ExperimentalStdlibApi
fun main(){
    val plugins = Plugins.find("/Applications/STM32CubeIDE.app/Contents/Eclipse/plugins")
        ?: return println("Not Found: Plugins Folder")
    plugins.forEach {
        println("${it.key} ${it.value}")
    }
}