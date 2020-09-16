package me.syari.stm32.viewer.util

import javafx.concurrent.Task
import tornadofx.FXTask

infix fun <T> Task<T>.finally(func: () -> Unit) = apply {
    require(this is FXTask<*>) { "finally() called on non-FXTask subclass" }
    finally(func)
}