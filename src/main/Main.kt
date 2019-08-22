package main

import java.io.File
import java.net.URLClassLoader

fun main() {
    val testJarFile = File("test.jar")
    val testLoader = URLClassLoader.newInstance(arrayOf(testJarFile.toURL()))
    val testPlugin = testLoader.loadClass("plugins.testPlugin.Test").newInstance() as Plugin
    testPlugin.load()

    val test2JarFile = File("test2.jar")
    val test2Loader = URLClassLoader.newInstance(arrayOf(test2JarFile.toURL()))
    val test2Plugin = test2Loader.loadClass("plugins.testPlugin2.Test2").newInstance() as Plugin
    test2Plugin.load()
}
