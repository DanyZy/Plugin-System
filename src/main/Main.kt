package main

import IPlugin
import com.google.gson.*
import java.io.File
import java.io.FileFilter


fun main() {
    val pluginList = mutableListOf<IPlugin>()

    val directoryListing = File("src/plugins/").listFiles(FileFilter{PluginLoader.instance.maskFilter(it, "jar")})

    PluginLoader.instance.pluginListFilling(directoryListing, pluginList)

    val CM = ConfigManager("src/config/configList.json", Gson(), jsonString = mutableListOf())
    PluginLoader.instance.pluginListLoader(pluginList, CM)
    println("_______________________________")
    PluginLoader.instance.fromJsonPluginLoader(pluginList, CM)

    PluginLoader.instance.watchChanges("src/plugins/")
}
