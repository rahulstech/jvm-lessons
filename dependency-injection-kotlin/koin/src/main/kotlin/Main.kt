import org.koin.core.context.startKoin
import org.koin.core.error.ClosedScopeException
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.getKoin

fun basicExample() {
    // Start Koin with the module
    startKoin {

        // register modules
        modules(
            appModules
        )
    }

    var r = 10
    while (r-- > 0) {

        // GreetingMessage is added as factory. therefore on each call to the below function will create a new instance
        get<GreetingMessage>(GreetingMessage::class.java).greet("Rahul")

        // SingletonGreetingMessage is added as single in appModules module,
        // therefore only single instance is created and that instance is returned from each call to
        // the below function
        get<SingletonGreetingMessage>(SingletonGreetingMessage::class.java).greet("Rahul")
        println("==============================================================================")
    }
}

fun constructorDiExample() {
   startKoin {
       modules(
           fruitConsumerModule,
           fruitApiModule
       )
   }

    val consumer: FruitConsumer = get(FruitConsumer::class.java)
    consumer.showAllFruits()
}

fun constructorParameterInjection() {
    startKoin {
        modules(calculatorModule)
    }

    // example of parameterized get.
    // when to use parameterized get? when I have a complex factory method

    val addition1: Addition = get(Addition::class.java) { parametersOf(14,15) }
    println("sum of ${addition1.a} and ${addition1.b} = ${addition1.sum()}")

    val addition2: Addition = get(Addition::class.java) { parametersOf(25,17) }
    println("sum of ${addition2.a} and ${addition2.b} = ${addition2.sum()}")

    // example of inject

    val subtraction1: Subtraction = Calculator(17.0,19.0).subtraction
    println("subtraction of ${subtraction1.a} and ${subtraction1.b} = ${subtraction1.result()}")

    val subtraction2: Subtraction = Calculator(13.0,71.0).subtraction
    println("subtraction of ${subtraction2.a} and ${subtraction2.b} = ${subtraction2.result()}")
}

fun instanceByNamedDefinition() {
    startKoin {
        modules(animalFactModule)
    }

    val catFact: AnimalFact = get(AnimalFact::class.java, named("CatFact"))
    println(catFact.getRandomFact())

    val dogFact: AnimalFact = get(AnimalFact::class.java, named("DogFact"))
    println(dogFact.getRandomFact())
}

fun scopeExample() {
    startKoin {
        modules(dbAndViewModule)
    }

    val indConn: DBConnection = get(DBConnection::class.java)
    println("name of connection outside: ${indConn.name}")

    val view1: ViewScopeComponent = get(ViewScopeComponent::class.java)

    view1.show()
    view1.hide()

    runCatching {
        val conn: DBConnection = view1.scope.get()
        println("name of connection of view1 scope ${conn.name}")
    }
        .onFailure {
            if (it is ClosedScopeException) {
                println("view1 scope closed so can not get from scope")
            }
            else {
                it.printStackTrace()
            }
        }

    val view2: ViewNotScopeComponent = get(ViewNotScopeComponent::class.java) { parametersOf(1) }

    // scopeId + scopeName = a unique scope instance. so if I use same scope name but different scope id then new scope will be created
    // no two different scope name can not take same scopeId, it will be replaced.
    // in module definition scopeName is used not the scopeId. see dbAndViewModule in scopes.kt
    // scopeId is used in getScope
    val scope2 = getKoin().getOrCreateScope("ViewNotScopeComponent", named("ViewNotScopeComponent"))
    scope2.registerCallback(object : ScopeCallback{
        override fun onScopeClose(scope: Scope) {
            view2.dbConn.close()
        }
    })

    view2.show()

    // if I close this scope here then a new scope instance will be created for scope3
   //  scope2.close()

    runCatching {
        val conn: DBConnection = scope2.get()
        println("name of connection of view2 scope ${conn.name}")
    }
        .onFailure {
            if (it is ClosedScopeException) {
                println("view2 scope closed so can not get from scope")
            }
            else {
                it.printStackTrace()
            }
        }

    val view3: ViewNotScopeComponent = get(ViewNotScopeComponent::class.java) { parametersOf(2) }

    // since scope2 is not closed therefore scope2 and scope3 is same instance.
    // scope "ViewNotScopeComponent" is already exists therefore both view2 and view3 shares the same db connection
    val scope3 = getKoin().getOrCreateScope("ViewNotScopeComponent", named("ViewNotScopeComponent"))

    // if scope2 closed then view2 db connection and view3 db connection names are different
    //          not closed then both are same
    // since view3 db connection is never explicitly closed therefore it will be connected i.e. isConnected = true
    view3.show()

    // the independent DBConnection i.e. DBConnection which is out of any scope
    // is not affected by the scoped db connections. so, isConnected = true
    println("connected status of independent connection: ${indConn.isConnected}")
}

fun main(args: Array<String>) {
//    basicExample()

//    constructorDiExample()

//    constructorParameterInjection()

//    instanceByNamedDefinition()

    scopeExample()

}
