package com.viraj.spendora

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var db: ExpenseDatabase

    private var expenseId = 0

    private val categories = arrayOf(
        "Food",
        "Transport",
        "Shopping",
        "Bills",
        "Entertainment",
        "Health",
        "Personal",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "spendora_db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        etAmount = findViewById(R.id.etAmount)
        spCategory = findViewById(R.id.spCategory)
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spCategory.adapter = adapter

        expenseId = intent.getIntExtra("id", 0)

        if (expenseId != 0) {
            etAmount.setText(intent.getDoubleExtra("amount", 0.0).toString())
            etNote.setText(intent.getStringExtra("note"))

            val category = intent.getStringExtra("category")
            val position = categories.indexOf(category)

            if (position >= 0) {
                spCategory.setSelection(position)
            }
        }

        btnSave.setOnClickListener {
            saveExpenseWithLocation()
        }
    }

    private fun saveExpenseWithLocation() {
        val amountText = etAmount.text.toString()
        val category = spCategory.selectedItem.toString()
        val note = etNote.text.toString()

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        btnSave.isEnabled = false
        btnSave.text = "Fetching location..."

        val locationHelper = LocationHelper(this)

        locationHelper.getCurrentLocation { location ->

            val expense = Expense(
                id = expenseId,
                amount = amountText.toDouble(),
                category = category,
                note = note,
                date = System.currentTimeMillis(),
                location = location
            )

            if (expenseId == 0) {
                db.expenseDao().insertExpense(expense)
                Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show()
            } else {
                db.expenseDao().updateExpense(expense)
                Toast.makeText(this, "Expense Updated!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}