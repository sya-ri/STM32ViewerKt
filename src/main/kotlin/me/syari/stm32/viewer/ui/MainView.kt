package me.syari.stm32.viewer.ui

import javafx.scene.Parent
import tornadofx.View

class MainView : View("STM32ViewerKt") {
    override val root: Parent by fxml("/fxml/MainView.fxml")
}
