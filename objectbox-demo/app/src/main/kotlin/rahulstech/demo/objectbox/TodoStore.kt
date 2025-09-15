package rahulstech.demo.objectbox

import io.objectbox.BoxStore
import rahulstech.demo.objectbox.entity.MyObjectBox

object TodoStore {
    val store: BoxStore by lazy {
        MyObjectBox.builder()
            .name("todo-store")
            .build()
    }
}
