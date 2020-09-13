package me.syari.stm32.viewer.config

import java.io.File
import java.util.*

open class ConfigFile(private val file: File) {
    private val properties = Properties()

    fun key(name: String) = Key(this, name)

    fun load() {
        if (file.exists()) {
            properties.load(file.inputStream())
        }
    }

    fun get(key: Key): String? {
        return properties.getProperty(key.name)
    }

    fun put(key: Key, value: String?) {
        properties[key.name] = value
    }

    private fun saveFile() {
        if (!file.exists()) {
            file.createNewFile()
        }
        properties.store(file.outputStream(), "STM32ViewerKt")
    }

    fun saveFile(run: ConfigFile.() -> Unit) {
        run(this)
        saveFile()
    }

    class Key(private val configFile: ConfigFile, val name: String) {
        fun get() = configFile.get(this)

        fun put(value: String?) = configFile.put(this, value)
    }
}