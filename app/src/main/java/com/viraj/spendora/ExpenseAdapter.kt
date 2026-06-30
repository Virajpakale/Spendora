package com.viraj.spendora

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenseList: List<Expense>,
    private val onDelete: (Expense) -> Unit,
    private val onEdit: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.tvCategory.text = expense.category
        holder.tvAmount.text = "₹${expense.amount}"
        holder.tvNote.text = expense.note
        holder.tvLocation.text = "📍 ${expense.location}"

        holder.btnEdit.setOnClickListener {
            onEdit(expense)
        }

        holder.btnDelete.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialog_delete, null)

            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
            val btnConfirmDelete = dialogView.findViewById<Button>(R.id.btnConfirmDelete)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnConfirmDelete.setOnClickListener {
                onDelete(expense)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }
}