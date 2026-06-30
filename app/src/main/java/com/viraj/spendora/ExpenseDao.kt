package com.viraj.spendora

import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert
    fun insertExpense(expense: Expense)

    @Update
    fun updateExpense(expense: Expense)

    @Delete
    fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date >= :start AND date <= :end")
    fun getExpensesBetween(start: Long, end: Long): List<Expense>
}