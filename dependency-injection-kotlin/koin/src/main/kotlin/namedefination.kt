import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

interface AnimalFact {

    fun getRandomFact(): String
}

class DogFactImpl : AnimalFact {

    override fun getRandomFact(): String = "this is a random fact about dog"
}

class CatFactImpl : AnimalFact {
    override fun getRandomFact(): String = "this is a random fact about cat"
}

val animalFactModule = module {
    /**
     * I want random facts about different types of animals. getRandomFact of AnimalFact interface returns
     * random fact about animal depending on its implementation. There are two implementations
     * DogFactImpl: provides random dog fact
     * CatFactImpl: provides random cat fact
     *
     * if I do the following
     *
     * single<AnimalFact> { DogFactImpl() }
     *
     * single<AnimalFact> { CatFactImpl() }
     *
     * then, fact will be an instance of CatFactImpl always.
     *
     * val fact: AnimalFact = get(AnimalFact::class.java)
     *
     * The solution is named definition as below. To get instance of particular definition
     * I need to use the name like below
     *
     * val catFact: AnimalFact = get(AnimalFact::class.java, named("CatFact")) // returns CalFactImpl
     * val dogFact: AnimalFact = get(AnimalFact::class.java, named("DogFact")) // returns DogFactImpl
     *
     * Use cases
     * =========
     *
     * When I have a base class and multiple definitions i.e. implementations for different purposes; but I want to use those
     * implementations by the base class only, then I have to use named definition like this.
     *
     * When not to use
     * ===============
     *
     * When I have different implementations for different environments: Production , Development, Test etc.
     */

    single<AnimalFact>(named("DogFact")) { DogFactImpl() }

    // purpose of use bind is same as above where I have used generic.
    // Why I need these? in get or inject I can simply use the AnimalFact::class and
    // I don't need to know what is the name of implementation class.
    // in future even if I change the implementation class, I don't need to change any places
    // where I am using that implementation.
    //
    // if I omit the bind or generic then these "single" methods will return DogFactImpl and CatFactImpl respectively
    // therefore in future if I want to use different implementation for "DogFact" then I also have to change all the places
    // where I am using this "DogFact".
    single(named("CatFact")) { CatFactImpl() } bind AnimalFact::class
}