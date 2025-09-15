import java.time.LocalDate
import java.util.UUID

enum class IncomeExpenseType {
    INCOME, EXPENSE
}

data class IncomeExpense(
    var type: IncomeExpenseType, var amount: Float, var date: LocalDate, var note: String? = null,
    val id: String = UUID.randomUUID().toString()) {

    override fun toString(): String {
        return "$type id: $id date: $date amount: $amount note: $note "
    }
}

class IncomeExpenseDao {

    private val callbackToString: (Any) -> String = {
        val data = it as IncomeExpense
        "${data.type},${data.amount},${data.date},${data.note},${data.id}"
    }

    private val callbackFromString: (String) -> Any = {
        val cols = it.split(",")
        val type = IncomeExpenseType.valueOf(cols[0])
        val amount = cols[1].toFloat()
        val date = LocalDate.parse(cols[2])
        val note = cols[3]
        val id = cols[4]
        IncomeExpense(type, amount, date, note, id)
    }

    private val entries: MutableMap<String, IncomeExpense> = mutableMapOf()
    private val storage: Storage =
        StorageFactory.csvFileStorage("./income_expense_data.csv", callbackToString, callbackFromString)

    init {
        val data = storage.readAll()?.associate {
            val data = (it as IncomeExpense)
            data.id to data
        }
        if (null != data) entries.putAll(data)
    }

    fun store() {
        storage.writeAll(entries.values.toList())
    }

    fun save(data: IncomeExpense): Boolean {
        entries[data.id] = data
        return true;
    }

    fun getAll(type: IncomeExpenseType?): List<IncomeExpense> {
        if (type == null) {
            return entries.values.toList()
        }
        return entries.filter {
            type == it.value.type
        }.values.toList()
    }

    fun getAllBetween(start: LocalDate, end: LocalDate, type: IncomeExpenseType?): List<IncomeExpense> {
        return entries.filterValues {
            isDateInRange(it.date, start, end) && (null == type || type == it.type)
        }.values.toList()
    }

    fun getById(id: String): IncomeExpense? = entries[id]

    fun remove(data: IncomeExpense) = entries.remove(data.id)

    fun createMonthlyReportByType(start: LocalDate, end: LocalDate, type: IncomeExpenseType): Map<LocalDate, Float> {
        val map = entries.filterValues {
            type == it.type && isDateInRange(it.date, start, end)
        }
        val report = mutableMapOf<LocalDate, Float>()
        for (entry in map) {
            val date = entry.value.date.withDayOfMonth(1)
            val amount = entry.value.amount
            val sum = report.computeIfAbsent(date) { 0f }
            report[date] = sum + amount
        }
        return report
    }

    private fun isDateInRange(date: LocalDate, start: LocalDate, end: LocalDate): Boolean =
        date.isEqual(start) || date.isEqual(end) || (date.isAfter(start) && date.isBefore(end))
}
