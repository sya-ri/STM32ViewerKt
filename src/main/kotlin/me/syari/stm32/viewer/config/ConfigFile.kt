package me.syari.stm32.viewer.config

import java.io.File
import java.util.*

open class ConfigFile(private val file: File) {
    private val properties = Properties()

    fun <T> key(name: String, type: Key.Type<T>) = Key(this, name, type)

    fun load() {
        if (file.exists()) {
            properties.load(file.inputStream())
        }
    }

    fun <T> get(key: Key<T>): T? {
        return key.type.parse(properties.getProperty(key.name))
    }

    fun <T> put(key: Key<T>, value: T?) {
        properties[key.name] = key.type.unparse(value)
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

    class Key<T>(private val configFile: ConfigFile, val name: String, val type: Type<T>) {
        fun get() = configFile.get(this)

        fun put(value: T?) = configFile.put(this, value)

        class Type<T>(val parse: (String?) -> T?, val unparse: (T?) -> String?) {
            companion object {
                val string = Type({ it }, { it })
            }
        }
    }
}