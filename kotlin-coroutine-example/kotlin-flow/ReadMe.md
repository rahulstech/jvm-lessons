## Kotlin Flow

### Projects

1. **CacheApiCall:** the [CacheApiCall](./src/main/kotlin/CacheApiCall.kt) demonstrate the scenario where we need to cache an api call locally.
2. **Combination:** the [Combination](./src/main/kotlin/Combination.kt) demonstrate combining results from two flows and returning a flow with combined result
### Learnings Outcome

- **Create Kotlin Flow:** create kotlin flow using `flow { }`. inside flow block `emit()` must be called to produce the result otherwise the value wil not be collected.
only on adding `collect { }` block the flow will run. that is why flow is called **cold**. collect is a suspend function so must be called inside a coroutine like `runBlocking {}` or `launch {}` etc.
- **Flow Intermediate:** sometimes the flow produces result need to be transformed. the intermediates help in this case. `transform {}` is one of them. in side transform we need to `emit` the transformed result.
transform returns a new flow. add collect to collect the transformed result
- **combine:** kotlin `combineTransform(Flow<>) {}` let use results from two flows and produce new result. for example: we are fetching results from two api endpoints and combining the results to create a compact result.
combined result must be emitted from transform.
- **emit vs emitAll:** use emit to emit a result. use emitAll to emit a Flow. 
- **onEmpty** when a flow returns without emitting then onEmpty is called. to handle the flow with no result cases we can use this. also we can `emit` default value from onEmpty
- **flowOn:** set dispatcher to run the flow. like for io related work we may use `Dispatchers.IO`. flow by default run on calling coroutine context.
- **catch:** catch throwable thrown in any stage of flow before it is specified. 