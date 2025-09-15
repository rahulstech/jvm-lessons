import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.core.scope.get
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

class DBConnection(val name: String) {

    var isConnected = false

    init {
        connect()
    }

    fun connect() {
        println("connection database with connection name: $name")
        isConnected = true
    }

    fun close() {
        println("closing database with connection name $name")
        isConnected = false
    }
}

open class View() {

    open fun show() = println("show view ${this.javaClass.simpleName}")

    open fun hide() = println("hide view ${this.javaClass.simpleName}")
}

class ViewScopeComponent() : KoinScopeComponent, View() {

    // lazily create scope
    override val scope: Scope by lazy {
        createScope(
            this // scope source
        )
    }

    // get DBConnection from current scope, since ViewScopeComponent is a KoinScopeComponent
    private val dbConnection: DBConnection by lazy {
        // get or scope.get() have same effect. similarly inject() and scope.inject()
        get { parametersOf("ConnectionOfViewScopeComponent") }
    }

    init {
        // close db connection as soon as this scope is closed
        scope.registerCallback(object : ScopeCallback{
            override fun onScopeClose(scope: Scope) = dbConnection.close()
        })
    }

    override fun show() {
        super.show()
        println("db connection name: ${dbConnection.name}")
    }

    override fun hide() {
        super.hide()
        scope.close()
    }
}

class ViewNotScopeComponent(val index: Int) : View() {

    // get db connection for "ViewNotScopeComponent" scope
    // Note: if no scope found for scope id then it will throw an error.
    //      I instantiated DBConnection lazily so that scope is created first
    //      in Main ( like scope2 and scope3) then it is instantiated when called first time. Otherwise,
    //      I can use getOrCreateScope
    val dbConn: DBConnection by lazy {
        getKoin().getScope("ViewNotScopeComponent")
            .get(DBConnection::class.java) { parametersOf("ConnectionViewNoScope$index") }
    }

    override fun show() {
        super.show()
        println("name of db connection inside ViewNotScopeComponent ${dbConn.name}")
    }
}

val dbAndViewModule = module {

    /**
     * Difference among single, factory and scoped
     * ===========================================
     *
     * single: only one instance for whole application lifecycle
     * scoped: only one instance for whole scope lifecycle
     * factory: create new instance every time
     */

    factory { (index: Int) -> ViewNotScopeComponent(index) }

    factory { ViewScopeComponent() }

    /**
     * scope<ViewScopeComponent> { } and
     * scope(named<ViewScopeComponent>()) { } and
     * scope(named("ViewScopeComponent")) { }
     *
     * these are the only three ways to create scope by name.
     * Note: named<ViewScopeComponent>() and named("ViewScopeComponent")
     */

    scope<ViewScopeComponent> {
        // scope of type ViewScopeComponent will get db connection of name "ConnectionViewScoped"
        scoped { DBConnection("ConnectionViewScoped") }
    }

    scope(named("ViewNotScopeComponent")) {
        // any scope with name "ViewNotScopeComponent", irrespective of type, will get db this connection
        scoped { (name: String) -> DBConnection(name)}
    }

    // db connections which are not part of any scope will get this connection
    single { DBConnection("IndependentConnection") }
}