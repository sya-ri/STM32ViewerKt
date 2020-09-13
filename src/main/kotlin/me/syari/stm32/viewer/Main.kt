package me.syari.stm32.viewer

import me.syari.stm32.viewer.config.Config
import me.syari.stm32.viewer.ui.MainView
import tornadofx.App

class Main : App(MainView::class) {
    init {
        Config.load()
    }
}
