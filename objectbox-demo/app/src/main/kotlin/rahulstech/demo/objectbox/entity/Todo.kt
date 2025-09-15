package rahulstech.demo.objectbox.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Todo(
    @Id(assignable = true)
    var id: Long = 0L,
    var content: String = ""
)