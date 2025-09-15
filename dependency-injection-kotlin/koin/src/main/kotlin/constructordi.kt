import org.koin.dsl.module

// api interface
interface FruitsApi {

    fun getFruits(): List<String>
}

// api implementation
class FruitApiImpl : FruitsApi {

    override fun getFruits(): List<String> {
        return listOf(
            "apple", "guava", "cherry", "dates"
        )
    }
}

class FruitConsumer(
    // constructor accepts any implementation of FruitApi interface.
    // I can pass different implementation for test, development and production environment.
    // here FruitConsumer is loosely coupled to FruitsApi: FruitConsumer needs FruitsApi;
    // but I can pass any concrete implementation for different purpose
    val fruitsApi: FruitsApi
) {
    fun showAllFruits() {
        val fruits = fruitsApi.getFruits()
        val s = fruits.joinToString(", ")
        println(s)
    }
}

val fruitApiModule = module {
    // FruitConsumer constructor needs FruitsApi; therefore I need to explicitly mention FruitsApi in factory generics
    factory<FruitsApi> { FruitApiImpl() }
}

val fruitConsumerModule = module {

    factory {
        FruitConsumer(
            // get will return FruitsApi instance from a module which provides an instance of FruitsApi implementation.
            // if no such module installed during startKoin then it will throw error.
            // I can simply change the module that provides the FruitsApi implementation in different environment in startKoin
            get()
    ) }
}