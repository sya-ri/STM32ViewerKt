package me.syari.stm32.viewer.util

import javafx.scene.Node
import javafx.scene.input.TransferMode
import java.io.File

inline fun Node.enableDragDropFile(crossinline fileCondition: (File) -> Boolean, crossinline action: (File?) -> Unit) {
    setOnDragOver {
        if (it.dragboard.files.firstOrNull(fileCondition) != null) {
            it.acceptTransferModes(TransferMode.MOVE)
        }
    }
    setOnDragDropped {
        action(it.dragboard.files.firstOrNull(fileCondition))
    }
}