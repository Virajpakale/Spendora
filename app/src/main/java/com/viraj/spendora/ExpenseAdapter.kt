package com.viraj.spendora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onDelete: (Expense) -> Unit,
    private val onEdit: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        val btnEdit: TextView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)

        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {
        val expense = expenses[position]

        // Basic info
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = "₹${expense.amount}"
        holder.tvNote.text = expense.note
        holder.tvLocation.text ="📍 ${expense.location}"

        // Date & Time formatting
        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

        val timeFormat = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        )

        holder.tvDate.text = dateFormat.format(expense.date)
        holder.tvTime.text = timeFormat.format(expense.date)

        // Edit click
        holder.btnEdit.setOnClickListener {
            onEdit(expense)
        }

        // Delete click
        holder.btnDelete.setOnClickListener {
            onDelete(expense)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}