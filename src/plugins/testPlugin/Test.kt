package plugins.testPlugin

import main.Plugin

abstract class Test : Plugin {

    override fun load() {
        println("test successful")
    }

}