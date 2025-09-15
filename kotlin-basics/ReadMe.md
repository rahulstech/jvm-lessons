## Kotlin Financial Terminal App

### What this application do?

- [X] Show a menu of actions
- [x] Add income and expense with note
- [x] Show by date and type
- [x] Edit income and expense
- [x] Delete income and expense
- [x] Create report: monthly, quarterly, yearly income, expense and profit
- [x] Save data in file

### Learnings from this project

1. **Console I/O**: Use `println()` `print()` for console output. For console input use `Scanner`
2. **Datatypes and variables and type casting**: Use `val` or `var` for defining variable. After assign `val` variable
   values can not be changed. But `var` variable value can be changed any number of time.
3. **Statement: conditional, loop etc.**: Use of `when`. `When` is like `switch` but not limited to constant value checking only. There is `null` and `else` branch. Foreach loop using `in` keyword, where left side
of the keyword is a single value and right side the source. source must be an iterable type. for index based looping
   we need you use range like this `1..3`. it creates an iterable containing 1, 2 and 3.
   Best part is `if` `when` can either execute the statement or return the value if assigned to a variable. For example

```kotlin
val n = 5

val x = if (n == 3) "b" else "d" // x will contain "b" or "d" depending on the value of n

// similarly

var y = when { // when subject can be added or omitted depending on the situation
    n%2 == 0 -> "b"
    n%2 != 0 -> "c"
    else -> "x" // if when subject is missing the else branch is required
}
```
4. **DataStructures: List, Map**: Use `listOf()`, `mapOf()` to create immutable list and map. Otherwise, use `mutableListOf()` and `mutableMapOf()` to create the mutable ones.
5. **OOPs**: Learned to create class, enum class, data class, object declaration, Openness of class. Creation of inline class implementation.
6. **File I/O**: File io using `File.bufferedWriter` and `File.bufferedReader()`
