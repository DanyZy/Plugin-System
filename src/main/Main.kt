package main

import java.io.File
import java.net.URLClassLoader

fun main(args: Array<String>) {
    println("Hello Kotlin")

    val testJarFile = File("test.jar")
    val testLoader = URLClassLoader.newInstance(arrayOf(testJarFile.toURL()))
    val testPlugin = testLoader.loadClass("plugins.testPlugin.Test").newInstance() as Plugin
    testPlugin.load()
}