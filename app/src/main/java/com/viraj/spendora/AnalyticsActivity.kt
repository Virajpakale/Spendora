package com.viraj.spendora

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var recyclerCategories: RecyclerView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvHighestMonth: TextView
    private lateinit var db: ExpenseDatabase

    private lateinit var tabWeek: TextView
    private lateinit var tabMonth: TextView
    private lateinit var tabYear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        recyclerCategories = findViewById(R.id.recyclerCategories)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvHighestMonth = findViewById(R.id.tvHighestMonth)

        tabWeek = findViewById(R.id.tabWeek)
        tabMonth = findViewById(R.id.tabMonth)
        tabYear = findViewById(R.id.tabYear)

        db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "spendora_db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        loadAnalytics("month")

        tabWeek.setOnClickListener {
            tabWeek.setBackgroundResource(R.drawable.bg_tab_selected)
            tabMonth.setBackgroundResource(R.drawable.bg_tab_unselected)
            tabYear.setBackgroundResource(R.drawable.bg_tab_unselected)

            loadAnalytics("week")
        }

        tabMonth.setOnClickListener {
            tabWeek.setBackgroundResource(R.drawable.bg_tab_unselected)
            tabMonth.setBackgroundResource(R.drawable.bg_tab_selected)
            tabYear.setBackgroundResource(R.drawable.bg_tab_unselected)

            loadAnalytics("month")
        }

        tabYear.setOnClickListener {
            tabWeek.setBackgroundResource(R.drawable.bg_tab_unselected)
            tabMonth.setBackgroundResource(R.drawable.bg_tab_unselected)
            tabYear.setBackgroundResource(R.drawable.bg_tab_selected)

            loadAnalytics("year")
        }
    }

    private fun loadAnalytics(type: String) {
        val allExpenses = db.expenseDao().getAllExpenses()
        val filteredExpenses = mutableListOf<Expense>()

        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        for (expense in allExpenses) {
            when (type) {
                "week" -> {
                    if (now - expense.date <= 7L * 24 * 60 * 60 * 1000) {
                        filteredExpenses.add(expense)
                    }
                }

                "month" -> {
                    calendar.timeInMillis = expense.date
                    val expenseMonth = calendar.get(Calendar.MONTH)

                    calendar.timeInMillis = now
                    val currentMonth = calendar.get(Calendar.MONTH)

                    if (expenseMonth == currentMonth) {
                        filteredExpenses.add(expense)
                    }
                }

                "year" -> {
                    calendar.timeInMillis = expense.date
                    val expenseYear = calendar.get(Calendar.YEAR)

                    calendar.timeInMillis = now
                    val currentYear = calendar.get(Calendar.YEAR)

                    if (expenseYear == currentYear) {
                        filteredExpenses.add(expense)
                    }
                }
            }
        }

        var totalSpent = 0.0
        val categoryMap = mutableMapOf<String, Double>()

        for (expense in filteredExpenses) {
            totalSpent += expense.amount
            categoryMap[expense.category] =
                categoryMap.getOrDefault(expense.category, 0.0) + expense.amount
        }

        tvTotalSpent.text = "₹${String.format("%.0f", totalSpent)}"

        val highest = categoryMap.values.maxOrNull() ?: 0.0
        tvHighestMonth.text = "₹${String.format("%.0f", highest)}"

        val categoryItems = mutableListOf<CategoryItem>()

        for ((category, amount) in categoryMap) {
            val percent =
                if (totalSpent == 0.0) 0 else ((amount / totalSpent) * 100).toInt()

            val icon = when (category.lowercase()) {
                "shopping" -> "🛍"
                "food" -> "🍔"
                "bills" -> "📄"
                "transport" -> "🚗"
                "health" -> "💊"
                else -> "✨"
            }

            categoryItems.add(
                CategoryItem(icon, category, amount, percent)
            )
        }

        recyclerCategories.layoutManager = LinearLayoutManager(this)
        recyclerCategories.adapter = CategoryAdapter(categoryItems)
    }
}