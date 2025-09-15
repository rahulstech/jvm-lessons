## Word Counter using Kotlin Coroutines

This app simples counts number of time a word appears in piece of text.

### Learnings form this project
- **Creating and using coroutines:** `async` `lunch` always needs a coroutine scope. if these are all inside `runBlocking` or other async or lunch then it takes the parent score. Otherwise we need to use `GlobalScope.async` or `GlobalScope.lunch`
inside coroutine block `this` keyword returns the current coroutine score. 
- **Working with coroutine result:** async returns `Deffered<T>`. it is a type of `Job`. Job has many useful function like
`join` when called then the called thread is hanged till the coroutine finished
`cancel` to cancel a coroutine. Deffered has `await` which is same as join but also returns a value the same value returned from the async block. 
also there is `cancelAndJoin` which is nothing but a combination of cancel and join method
