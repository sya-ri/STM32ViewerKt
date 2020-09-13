package me.syari.stm32.viewer

import javafx.application.Application
import javafx.scene.text.FontWeight
import tornadofx.*

fun main() = Application.launch(HelloWorldApp::class.java)

class HelloWorldApp: App(HelloWorld::class, Styles::class)

class HelloWorld: View() {
    override val root = hbox {
        label("Hello world")
    }
}

class Styles: Stylesheet() {
    init {
        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }
    }
}