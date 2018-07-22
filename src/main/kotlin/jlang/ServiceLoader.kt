package jlang

class ServiceLoader {
    interface Human {
        fun say()
    }

    class Male : Human {
        override fun say() {
            println("Male say...")
        }
    }

    class Female : Human {
        override fun say() {
            println("Female say...")
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var loaders = java.util.ServiceLoader.load(Human::class.java)
            for (loader in loaders) {
                loader.say()
            }
        }
    }
}