package com.viraj.spendora

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.material.button.MaterialButton

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var db: ExpenseDatabase

    private var expenseId = 0
    private var selectedCategory = "Food"

    private lateinit var categoryButtons: List<MaterialButton>

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
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSaveExpense)

        val btnFood = findViewById<MaterialButton>(R.id.btnFood)
        val btnTransport = findViewById<MaterialButton>(R.id.btnTransport)
        val btnShopping = findViewById<MaterialButton>(R.id.btnShopping)
        val btnEntertainment = findViewById<MaterialButton>(R.id.btnEntertainment)
        val btnBills = findViewById<MaterialButton>(R.id.btnBills)
        val btnHealth = findViewById<MaterialButton>(R.id.btnHealth)
        val btnPersonal = findViewById<MaterialButton>(R.id.btnPersonal)
        val btnOther = findViewById<MaterialButton>(R.id.btnOther)

        categoryButtons = listOf(
            btnFood,
            btnTransport,
            btnShopping,
            btnEntertainment,
            btnBills,
            btnHealth,
            btnPersonal,
            btnOther
        )

        btnFood.setOnClickListener {
            selectedCategory = "Food"
            highlightSelected(btnFood)
        }

        btnTransport.setOnClickListener {
            selectedCategory = "Transport"
            highlightSelected(btnTransport)
        }

        btnShopping.setOnClickListener {
            selectedCategory = "Shopping"
            highlightSelected(btnShopping)
        }

        btnEntertainment.setOnClickListener {
            selectedCategory = "Entertainment"
            highlightSelected(btnEntertainment)
        }

        btnBills.setOnClickListener {
            selectedCategory = "Bills"
            highlightSelected(btnBills)
        }

        btnHealth.setOnClickListener {
            selectedCategory = "Health"
            highlightSelected(btnHealth)
        }

        btnPersonal.setOnClickListener {
            selectedCategory = "Personal"
            highlightSelected(btnPersonal)
        }

        btnOther.setOnClickListener {
            selectedCategory = "Other"
            highlightSelected(btnOther)
        }

        // Default selected
        highlightSelected(btnFood)

        // Edit mode
        expenseId = intent.getIntExtra("id", 0)

        if (expenseId != 0) {
            etAmount.setText(
                intent.getDoubleExtra("amount", 0.0).toString()
            )
            etNote.setText(intent.getStringExtra("note"))

            selectedCategory =
                intent.getStringExtra("category") ?: "Food"
        }

        btnSave.setOnClickListener {
            saveExpenseWithLocation()
        }
    }

    private fun highlightSelected(selectedButton: MaterialButton) {
        for (button in categoryButtons) {
            button.strokeWidth = 0
            button.alpha = 1f
        }

        selectedButton.strokeWidth = 2
        selectedButton.strokeColor =
            ContextCompat.getColorStateList(this, R.color.orange)
    }

    private fun saveExpenseWithLocation() {
        val amountText = etAmount.text.toString()
        val note = etNote.text.toString()

        if (amountText.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter amount",
                Toast.LENGTH_SHORT
            ).show()
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
                category = selectedCategory,
                note = note,
                date = System.currentTimeMillis(),
                location = location
            )

            if (expenseId == 0) {
                db.expenseDao().insertExpense(expense)

                Toast.makeText(
                    this,
                    "Expense Saved!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db.expenseDao().updateExpense(expense)

                Toast.makeText(
                    this,
                    "Expense Updated!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        }
    }
}