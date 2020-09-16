package me.syari.stm32.viewer.util

import javafx.concurrent.Task

class TaskContainer<T>(private val onCancel: () -> Unit) {
    var task: Task<T>? = null

    fun cancel() {
        task?.let {
            task = null
            onCancel.invoke()
        }
    }
}