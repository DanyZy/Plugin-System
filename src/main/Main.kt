package main

import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.io.File
import java.io.FileFilter


fun main() {
    val pluginList = mutableListOf<Class<*>>()
    val directoryListing = File("src/plugins/").listFiles(FileFilter{jarFilter(it)})
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
                val plugin = classLoader.loadClass(className)
                pluginList.add(plugin)
            }
        }
    }

    pluginLoader(pluginList)
}

fun jarFilter(file: File): Boolean {
    return file.isFile && file.name.endsWith(".jar")
}

fun pluginLoader (plugins: List<Class<*>>) {
    val tempList = mutableListOf<Class<*>>()

    for (plugin in plugins) {
        try {
            val method = plugin.getDeclaredMethod("load")
            method.invoke(plugin.newInstance())
        } catch (e: Exception) {
            tempList.add(plugin)
        }
    }

    if (tempList.isNotEmpty() && tempList != plugins) {
        pluginLoader(tempList)
    }
}