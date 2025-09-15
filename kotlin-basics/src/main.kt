import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Scanner
import kotlin.math.min

class FinBookApp {
    private val FORMATTER = DateTimeFormatter.ISO_DATE
    private val FORMAT_MONTH_YEAR = DateTimeFormatter.ofPattern("MMM-yyyy")
    private val input: Scanner = Scanner(System.`in`)
    val inexDao = IncomeExpenseDao()

    fun start() {
        var choice: Int?
        do {
            println("========= [ FinBook ] =========")
            println("1. Add Income/Expense")
            println("2. Show Income/Expense")
            println("3. Edit Income/Expense")
            println("4. Delete Income/Expense")
            println("5. Create Report")
            println("0. Exit")

            print(">>> ")
            choice = input.nextInt()
            input.nextLine()
            when (choice) {
                0 -> {
                    stop()
                    println("Good Bye")
                    break
                }

                1 -> addIncomeExpense()
                2 -> showIncomeExpense()
                3 -> editIncomeExpense()
                4 -> deleteIncomeExpense()
                5 -> makeReport()
            }
        } while (true)
    }

    private fun stop() = inexDao.store()

    private fun printHeader(title: String) {
        println("--------- ( $title ) ---------")
    }

    private fun addIncomeExpense() {
        printHeader("Add Income Expense")
        print("I - Income | E - Expense : ")
        val typeChoice = input.nextLine()
        print("Amount: ")
        val amount = input.nextFloat()
        input.nextLine()
        print("Date (yyyy-mm-dd): ")
        val dateString = input.nextLine()
        print("Note: ")
        val note = input.nextLine()
        val date = LocalDate.parse(dateString, FORMATTER)
        val inex = when (typeChoice) {
            "I" -> IncomeExpense(IncomeExpenseType.INCOME, amount, date, note)
            "E" -> IncomeExpense(IncomeExpenseType.EXPENSE, amount, date, note)
            else -> null
        }
        println()
        if (null != inex && inexDao.save(inex)) {
            println("new record saved")
        } else {
            println("new record not saved")
        }
    }

    private fun showIncomeExpense() {
        printHeader("Show income expense")
        println("1. Show income between dates")
        println("2. Show all income")
        println("3. Show expense between dates")
        println("4. Show all expense")
        println("5. Show income and expense between dates")
        println("6. Show all income and expense")
        println("0. Exit Menu")
        print(">>> ")
        val choice = input.nextInt()
        input.nextLine()
        when (choice) {
            1 -> showIncomeExpenseBetweenDates(IncomeExpenseType.INCOME)
            2 -> showAllIncomeExpense(IncomeExpenseType.INCOME)
            3 -> showIncomeExpenseBetweenDates(IncomeExpenseType.EXPENSE)
            4 -> showAllIncomeExpense(IncomeExpenseType.EXPENSE)
            5 -> showIncomeExpenseBetweenDates()
            6 -> showAllIncomeExpense()
        }
    }

    private fun showIncomeExpenseBetweenDates(incomeExpenseType: IncomeExpenseType? = null) {
        print("Start Date (yyyy-mm-dd): ")
        val startDateString = input.nextLine()
        print("End Date (yyyy-mm-dd): ")
        val endDateString = input.nextLine()

        val start = LocalDate.parse(startDateString, FORMATTER)
        val end = LocalDate.parse(endDateString, FORMATTER)
        val entries = inexDao.getAllBetween(start,end,incomeExpenseType)
        printIncomeExpenseList(entries, 0, 50)
        println()
    }

    private fun showAllIncomeExpense(incomeExpenseType: IncomeExpenseType? = null) {
        val entries = inexDao.getAll(incomeExpenseType)
        printIncomeExpenseList(entries, 0, 50)
        println()
    }

    private fun printIncomeExpenseList(list: List<IncomeExpense>, start: Int, len: Int) {
        val size = list.size
        if (size == 0) {
            return
        }
        val end = min( start + len - 1, size - 1)
        println("\nShowing results from ${start + 1} to ${end + 1}\n")
        for (x in start..end) {
            val inex = list[x]
            println("[${x + 1}] $inex ")
        }
        if (end == size - 1) {
            return
        }
        println("p - previous | n - next | q - quit")
        print(">>> ")
        val choice = input.nextLine()
        when (choice) {
            "p" -> printIncomeExpenseList(list, start-len, len)
            "n" -> printIncomeExpenseList(list, end+1, len)
        }
    }

    private fun editIncomeExpense() {
        printHeader("Edit Income Expense")
        print("Enter Id: ")
        val id = input.nextLine()
        val inex: IncomeExpense? = inexDao.getById(id)
        if (inex == null) {
            println()
            println("no record found")
        } else {
            val inexcpy = inex.copy()
            println()
            print("Press enter key if no change")
            println()
            println("Amount: ${inexcpy.amount}")
            print("Amount: ")
            if (input.hasNextFloat()) {
                val amount = input.nextFloat()
                input.nextLine()
                inexcpy.amount = amount
            }
            println("Date: ${inexcpy.date.format(FORMATTER)}")
            print("Date (yyyy-mm-dd) : ")
            val dateString = input.nextLine()
            if (dateString != "") {
                inexcpy.date = LocalDate.parse(dateString, FORMATTER)
            }
            println("Node: ${inexcpy.note}")
            print("Note: ")
            inexcpy.note = input.nextLine()

            println()
            println("changes saved")
            println()
        }
    }

    private fun deleteIncomeExpense() {
        printHeader("Delete Income Expense")
        print("Enter Id: ")
        val id = input.nextLine()
        val inex = inexDao.getById(id)
        if (inex == null) {
            println()
            println("no record found")
        }
        else {
            inexDao.remove(inex)
            println()
            println("record deleted")
            println()
        }
    }

    private fun makeReport() {
        printHeader("Report Income Expense")
        while (true) {
            println("M - Monthly")
            println("Q - Quarterly")
            println("Y - Yearly")
            println("X - Quit")
            print(">>> ")
            val grouping = input.nextLine()
            if (grouping == "X") {
                break
            }
            println("I - Income")
            println("E - Expense")
            println("R - Previous Menu")
            println("X - Quit")
            print(">>> ")
            val type = when (input.nextLine()) {
                "R" -> continue
                "X" -> break
                "I" -> IncomeExpenseType.INCOME
                "E" -> IncomeExpenseType.EXPENSE
                else -> {
                    println("invalid choice")
                    break
                }
            }
            when (grouping) {
                "M" -> {
                    val dateEnd = LocalDate.now()
                    val dateStart = dateEnd.minusYears(1).withDayOfMonth(1)
                    val entries = inexDao.createMonthlyReportByType(dateStart, dateEnd, type)
                    entries.forEach { (date, amount) ->
                        println("${date.format(FORMAT_MONTH_YEAR)} $amount")
                    }
                    break
                }

                "Q" -> {
                    println("Enter Financial Year. Example FY2024 = 2024-04-01 - 2025-03-31")
                    print("FY: ")
                    val fy = input.nextInt()
                    val dateStart = LocalDate.of(fy - 1, 4,1)
                    val dateEnd = LocalDate.of(fy, 3, 31)
                    val entries = inexDao.getAllBetween(dateStart, dateEnd, type)

                    val quarterlyReport = mutableMapOf<String,Float>()

                    val quarters = mapOf(
                        "Q1" to (4..6),
                        "Q2" to (7..9),
                        "Q3" to (10..12),
                        "Q4" to (1..3)
                    )

                    for ((qname, months) in quarters) {
                        val qdata = entries.filter { it.date.monthValue in months }.map { it.amount }
                        val sum = qdata.sum()
                        quarterlyReport.put(qname, sum)
                    }
                    quarterlyReport.forEach { (qname, amount) -> println("$qname $amount") }
                    break
                }

                "Y" -> {
                    print("Enter Year: ")
                    val year = input.nextInt()
                    val dateStart = LocalDate.of(year, 1,1)
                    val dateEnd = LocalDate.of(year, 12, 31)
                    val entries = inexDao.getAllBetween(dateStart, dateEnd, type).map { it.amount }
                    val sum = entries.sum()
                    println("$dateStart - $dateEnd = $sum")
                    break
                }
            }
        }
    }
}

fun main() {
    val app = FinBookApp()
    app.start()
}