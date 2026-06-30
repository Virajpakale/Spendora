package com.viraj.spendora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var db: ExpenseDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvStreakCount: TextView
    private lateinit var btnAddExpense: ImageView
    private lateinit var btnAnalytics: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Auto update checker
        UpdateChecker(this).checkForUpdates()

        db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "spendora_db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        recyclerView = findViewById(R.id.recyclerView)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvStreakCount = findViewById(R.id.tvStreakCount)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnAnalytics = findViewById(R.id.btnAnalytics)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add Expense
        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // Open Analytics
        btnAnalytics.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }

        loadExpenses()
        calculateNoSpendStreak()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
        calculateNoSpendStreak()
    }

    private fun loadExpenses() {
        val expenses = db.expenseDao().getAllExpenses()

        // Total spend
        val total = expenses.sumOf { it.amount }
        tvTotalSpent.text = "₹${String.format("%.0f", total)}"

        recyclerView.adapter = ExpenseAdapter(
            expenses,
            onDelete = { expense ->
                db.expenseDao().deleteExpense(expense)
                loadExpenses()
                calculateNoSpendStreak()
            },
            onEdit = { expense ->
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra("id", expense.id)
                intent.putExtra("amount", expense.amount)
                intent.putExtra("category", expense.category)
                intent.putExtra("note", expense.note)
                startActivity(intent)
            }
        )
    }

    private fun calculateNoSpendStreak() {
        val expenses = db.expenseDao().getAllExpenses()

        if (expenses.isEmpty()) {
            tvStreakCount.text = "🔥 0 Day Streak"
            return
        }

        val latestExpenseDate = expenses.first().date
        val currentDate = System.currentTimeMillis()

        val diff = currentDate - latestExpenseDate
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        tvStreakCount.text = "🔥 $days Day Streak"
    }
}