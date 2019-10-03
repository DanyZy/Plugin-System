package main

import com.google.gson.Gson
import java.io.File

class ConfigManager(configPath: String, val gson: Gson, var jsonString: MutableList<String>) {
    val pluginConfigFile = File(configPath)
}