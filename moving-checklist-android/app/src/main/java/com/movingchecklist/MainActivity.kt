package com.movingchecklist

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.movingchecklist.adapter.BoardAdapter
import com.movingchecklist.data.DataManager
import com.movingchecklist.databinding.ActivityMainBinding
import com.movingchecklist.databinding.DialogCardBinding
import com.movingchecklist.model.Card
import com.movingchecklist.model.Column
import com.movingchecklist.model.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataManager: DataManager
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var columns: MutableList<Column>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowSubtitleEnabled(true)

        dataManager = DataManager(this)
        columns = dataManager.loadColumns()

        setupBoard()

        binding.fabAddColumn.setOnClickListener { showAddColumnDialog() }
    }

    private fun setupBoard() {
        boardAdapter = BoardAdapter(
            columns = columns,
            onCardAdd = { colIdx ->
                showCardDialog(colIdx, cardIdx = null)
            },
            onCardEdit = { colIdx, cardIdx ->
                showCardDialog(colIdx, cardIdx)
            },
            onCardDelete = { colIdx, cardIdx ->
                if (cardIdx in columns[colIdx].cards.indices) {
                    columns[colIdx].cards.removeAt(cardIdx)
                    save()
                    boardAdapter.notifyItemChanged(colIdx)
                    updateSubtitle()
                }
            },
            onCardMoveNext = { colIdx, cardIdx ->
                if (columns.size > 1 && cardIdx in columns[colIdx].cards.indices) {
                    val toColIdx = (colIdx + 1) % columns.size
                    val card = columns[colIdx].cards.removeAt(cardIdx)
                    columns[toColIdx].cards.add(card)
                    save()
                    boardAdapter.notifyItemChanged(colIdx)
                    boardAdapter.notifyItemChanged(toColIdx)
                    updateSubtitle()
                }
            },
            onColumnDelete = { colIdx ->
                val name = columns[colIdx].title
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete list")
                    .setMessage("Delete \"$name\" and all its ${columns[colIdx].cards.size} card(s)?")
                    .setPositiveButton("Delete") { _, _ ->
                        columns.removeAt(colIdx)
                        save()
                        boardAdapter.notifyItemRemoved(colIdx)
                        boardAdapter.notifyItemRangeChanged(colIdx, columns.size - colIdx)
                        updateSubtitle()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onReorder = { save() }
        )

        binding.rvBoard.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = boardAdapter
        }

        updateSubtitle()
    }

    private fun showCardDialog(colIdx: Int, cardIdx: Int?) {
        val isEdit = cardIdx != null
        val existing = if (isEdit) columns[colIdx].cards.getOrNull(cardIdx!!) else null

        val dialogBinding = DialogCardBinding.inflate(LayoutInflater.from(this))

        val priorities = Priority.values()
        val labels = priorities.map { it.label }
        dialogBinding.spinnerPriority.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)

        if (existing != null) {
            dialogBinding.etCardText.setText(existing.text)
            dialogBinding.spinnerPriority.setSelection(priorities.indexOf(existing.priority))
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (isEdit) "Edit card" else "Add card")
            .setView(dialogBinding.root)
            .setPositiveButton(if (isEdit) "Save" else "Add") { _, _ ->
                val text = dialogBinding.etCardText.text?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                    Toast.makeText(this, "Please enter a task description", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val priority = priorities[dialogBinding.spinnerPriority.selectedItemPosition]
                if (isEdit && existing != null) {
                    existing.text     = text
                    existing.priority = priority
                } else {
                    columns[colIdx].cards.add(Card(text = text, priority = priority))
                }
                save()
                boardAdapter.notifyItemChanged(colIdx)
                updateSubtitle()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddColumnDialog() {
        val px16 = (16 * resources.displayMetrics.density).toInt()
        val input = TextInputEditText(this)
        val inputLayout = TextInputLayout(this).apply {
            hint = "List name"
            addView(input)
        }
        val container = FrameLayout(this).apply {
            setPadding(px16 * 2, px16, px16 * 2, px16 / 2)
            addView(inputLayout)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add new list")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text?.toString()?.trim()
                if (name.isNullOrEmpty()) return@setPositiveButton
                val colors = listOf("#5E6C84","#0052CC","#36B37E","#FF5630","#FF8B00","#6554C0")
                columns.add(Column(title = name, colorHex = colors[columns.size % colors.size]))
                save()
                boardAdapter.notifyItemInserted(columns.size - 1)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun save() = dataManager.saveColumns(columns)

    private fun updateSubtitle() {
        val total = columns.sumOf { it.cards.size }
        val done  = columns.find { it.title.equals("done", ignoreCase = true) }?.cards?.size ?: 0
        val pct   = if (total > 0) done * 100 / total else 0
        supportActionBar?.subtitle = "$done / $total tasks done  ·  $pct% complete"
    }
}
