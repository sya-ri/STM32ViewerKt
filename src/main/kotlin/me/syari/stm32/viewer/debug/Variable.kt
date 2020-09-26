package me.syari.stm32.viewer.debug

data class Variable(val name: String, val typeName: String) {
    var value: String? = null
}