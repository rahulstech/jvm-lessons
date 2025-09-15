package rahulstech.demo.objectbox

import rahulstech.demo.objectbox.entity.Todo
import io.objectbox.Box

class App {
    val store by lazy { TodoStore.store }

    fun basic() {
        val box: Box<Todo> = store.boxFor(Todo::class.java)
        val todo = Todo(0,"todo")
        box.put(todo)

        val todo2 = Todo(15, "todo15")
        box.put(todo2)

        val todos = box.getAll()
        println(todos)
    }
}

fun main() {
    App().basic()
}