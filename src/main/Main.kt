package main

import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.io.File


fun main() {

    val directoryListing = File("src/plugins/").listFiles()
    if (directoryListing != null) {
        for (pluginSetPath in directoryListing) {
            if (pluginSetPath.name.endsWith(".jar")) {
                val pluginJarSet = JarFile(pluginSetPath)
                val entries = pluginJarSet.entries()

                val urls = arrayOf(URL("jar", "", -1, "file:$pluginSetPath!/"))
                val classLoader = URLClassLoader.newInstance(urls)

                while (entries.hasMoreElements()) {
                    val plugin = entries.nextElement()
                    if (plugin.isDirectory || !plugin.name.endsWith(".class")) {
                        continue
                    }
                    // -6 because of .class
                    var className = plugin.name.substring(0, plugin.name.length - 6)
                    className = className.replace('/', '.')
                    val clazz = classLoader.loadClass(className)
                    val methods = clazz.declaredMethods
                    for (method in methods) {
                        if (method.name == "load") {
                            method.invoke(clazz.newInstance())
                        }
                    }
                }
            } else {
                continue
            }
        }
    }
}