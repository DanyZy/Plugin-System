package plugins.testPlugin

import main.Plugin

class Test : Plugin() {
    override fun load() {
        println("test1 successful")
    }

}