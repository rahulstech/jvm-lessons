import org.koin.dsl.module

val appModules = module {
    // a new instance is created on each get() or inject() call
    factory { GreetingMessage() }

    // only one instance will be created
    single { SingletonGreetingMessage() }
}

class SingletonGreetingMessage() {
    init {
        println("initializing SingletonGreetingMessage")
    }

    fun greet(name: String) {
        println("singletonGreetingMessage: Hail $name")
    }
}

class GreetingMessage() {

    init {
        println("initializing GreetingMessage")
    }

    fun greet(name: String) {
        println("greetingMessage: Hello $name")
    }
}