import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class Addition(
    val a: Double,
    val b: Double,
) {
    fun sum(): Double = a + b
}

class Subtraction(
    val a: Double,
    val b: Double
) {
    fun result(): Double = a - b
}

// inject can be used inside KoinComponent only
class Calculator(val a: Double, val b: Double) : KoinComponent {

    // Note this is a definition not instantiation, means the Subtract instance is not created here
    // but will be created when subtract is called first time. therefore below is the  wrong statement
    //
    // val subtraction = Subtraction by inject { parametersOf(a,b) }
    //
    val subtraction : Subtraction by inject { parametersOf(a,b) }
}

val calculatorModule = module {

    // factory or single I can use any of these
    // Note: a callback is passed to factory create instance based on parameter.
    // the following will case error
    // factory { Addition(get(), get()) }
    // because get() returns a dependency not parameter.

    factory { (a: Double, b: Double) -> Addition(a,b) }

    factory { (a: Double, b: Double) -> Subtraction(a,b) }
}