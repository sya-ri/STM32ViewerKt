package me.syari.stm32.viewer

import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.ui.MainView
import tornadofx.App
import tornadofx.launch

class Main : App(MainView::class) {
    init {
        Config.load()
    }
}

fun main(args: Array<String>) {
    launch<Main>(args)
}
