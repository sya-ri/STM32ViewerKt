package me.syari.stm32.viewer.ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextField
import tornadofx.*
import java.io.File

class OptionView: View("My View") {
    override val root: Parent by fxml("/fxml/OptionView.fxml")

    @FXML
    private lateinit var textViewCubeIDEPath: TextField

    fun findCubeIDE(e: ActionEvent) {
        val initialDirectory = File(textViewCubeIDEPath.text).parentFile
        val file = chooseFile(null, emptyArray(), initialDirectory)
        file.firstOrNull()?.let {
            textViewCubeIDEPath.text = it.path
        }
    }
}
