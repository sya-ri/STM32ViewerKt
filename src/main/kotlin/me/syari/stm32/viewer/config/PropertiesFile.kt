package me.syari.stm32.viewer.config

import java.io.File

open class PropertiesFile(private val file: File) {
    private val properties = java.util.Properties()

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

    fun saveFile(run: PropertiesFile.() -> Unit) {
        run(this)
        saveFile()
    }

    class Key(private val propertiesFile: PropertiesFile, val name: String) {
        fun get() = propertiesFile.get(this)

        fun put(value: String?) = propertiesFile.put(this, value)
    }
}