package main

import IPlugin
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.io.File
import java.io.BufferedReader
import java.io.IOException
import java.nio.file.*

class PluginLoader {

    companion object {
        val instance = PluginLoader()
    }

    fun pluginListFilling(directoryListing: Array<File>?, pluginList: MutableList<IPlugin>) {
        if (directoryListing != null) {
            for (pluginSetPath in directoryListing) {
                val pluginJarSet = JarFile(pluginSetPath)
                val entries = pluginJarSet.entries()

                val urls = arrayOf(URL("jar", "", -1, "file:$pluginSetPath!/"))
                val classLoader = URLClassLoader.newInstance(urls)

                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (entry.isDirectory || !entry.name.endsWith(".class")) {
                        continue
                    }
                    // -6 because of .class
                    var className = entry.name.substring(0, entry.name.length - 6)
                    className = className.replace('/', '.')
                    val pluginClass = classLoader.loadClass(className)
                    try {
                        val plugin = pluginClass.newInstance() as IPlugin
                        pluginList.add(plugin)
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
        }
    }

    fun fromJsonPluginLoader(plugins: List<IPlugin>, CM: ConfigManager) {
        val bufferedReader: BufferedReader = CM.pluginConfigFile.bufferedReader()
        val jsonOutput = bufferedReader.use { it.readText() }
        val pluginNameList = CM.gson.fromJson(jsonOutput, mutableListOf<String>().javaClass)
        for (pluginName in pluginNameList) {
            for (plugin in plugins) {
                if (plugin.javaClass.name == pluginName)
                    plugin.load()
            }
        }
    }

    fun maskFilter(file: File, mask: String): Boolean {
        return file.isFile && file.name.endsWith(".$mask")
    }

    private fun load(plugin: IPlugin) {
        plugin.load()
    }

    fun pluginListLoader(plugins: List<IPlugin>, CM: ConfigManager) {
        val tempList = mutableListOf<IPlugin>()

        for (plugin in plugins) {
            try {
                load(plugin)
                CM.jsonString.add(CM.gson.toJson(plugin.javaClass.name))
            } catch (e: Exception) {
                tempList.add(plugin)
            }
        }

        if (tempList.isNotEmpty() && tempList != plugins) {
            pluginListLoader(tempList, CM)
        }

        CM.pluginConfigFile.writeText(CM.jsonString.toString())
    }

    fun watchChanges(folder: String) {

        val filePath = Paths.get(folder)
        val watchService: WatchService

        try {
            watchService = FileSystems.getDefault().newWatchService()

            //listen for create ,delete and modify event kinds
            filePath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        while (true) {
            val key: WatchKey
            try {
                //return signaled key, meaning events occurred on the object
                key = watchService.take()
            } catch (ex: InterruptedException) {
                return
            }

            //retrieve all the accumulated events
            for (event in key.pollEvents()) {
                val kind = event.kind()

                println("kind " + kind.name())
                val path = event.context() as Path
                println(path.toString())
            }
            //resetting the key goes back ready state
            key.reset()
        }

    }
}