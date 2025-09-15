import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class User(val username: String, val dob: LocalDate) {
    override fun toString(): String = "User(username = $username, dob = $dob)"
}

data class Visit(val username: String, val place: String) {

    override fun toString(): String = "Visit(username =  $username, place = $place)"
}

data class UserWithVisit(val username: String, val dob: LocalDate, val place: String?) {

    override fun toString(): String = "UserWithVisit(username=$username, dob=$dob, place=$place)"
}

val apiOfUsers = mapOf(
    "ram" to User("ram", LocalDate.of(1998,2,25)),
    "antony" to User("antony", LocalDate.of(1989,7,24)),
    "rahim" to User("rahim", LocalDate.of(2000,9,29))
)

val apiOfLastVisits = mapOf(
    "ram" to Visit("ram", "berlin"),
    "antony" to Visit("antony", "paris"),
    "rahim" to Visit("rahim","delhi")
)

val cache = mutableMapOf<String, UserWithVisit>()

fun getUserByUserName(username: String): Flow<User?> = flow {
    println("user api thread ${Thread.currentThread().name}")
    val user = apiOfUsers[username]
    println("user api result for $username = $user")
    emit(user)
}

fun getLastVisitOfUser(username: String): Flow<Visit?> = flow {
    println("visitor api thread ${Thread.currentThread().name}")
    val visit = apiOfLastVisits[username]
    println("visit api result $username = $visit")
    emit(visit)
}

fun main() {

    println("main thread ${Thread.currentThread().name}")

    while (true) {
        println("========================================")
        print("username: ")
        val username = readln()


        val mainFlow: Flow<UserWithVisit?> = flow {
                println("cache check thread ${Thread.currentThread().name}")
                val value = cache[username]
                if (null != value) {
                    println("cache hit $username = $value")
                    emit(value)
                } else {
                    println("cache miss $username")
                }
        }.onEmpty {
            println("empty cache result thread ${Thread.currentThread().name}")
                emitAll(getUserByUserName(username).combineTransform(getLastVisitOfUser(username)) { user, visit ->
                    println("api user and api visit both calls returns: user = $user, visit = $visit")
                    if (null != user) {
                        val userVisit = UserWithVisit(user.username, user.dob, visit?.place)
                        cache[username] = userVisit
                        println("successfully cached")
                        emit(userVisit)
                    }
                })
        }
            .flowOn(Dispatchers.IO)

        runBlocking {
            mainFlow.collect {
                println("collect thread ${Thread.currentThread().name}")
                println("user-visit: $it")
            }
        }
    }
}