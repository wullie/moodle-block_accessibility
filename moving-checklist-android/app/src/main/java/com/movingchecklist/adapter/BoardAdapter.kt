package com.movingchecklist.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movingchecklist.databinding.ItemColumnBinding
import com.movingchecklist.model.Column

class BoardAdapter(
    val columns: MutableList<Column>,
    private val onCardAdd: (colIdx: Int) -> Unit,
    private val onCardEdit: (colIdx: Int, cardIdx: Int) -> Unit,
    private val onCardDelete: (colIdx: Int, cardIdx: Int) -> Unit,
    private val onCardMoveNext: (colIdx: Int, cardIdx: Int) -> Unit,
    private val onColumnDelete: (colIdx: Int) -> Unit,
    private val onReorder: () -> Unit
) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemColumnBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val col = columns[position]
        with(holder.binding) {
            tvColumnTitle.text = col.title
            tvCardCount.text   = col.cards.size.toString()

            val dot = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(col.colorHex))
            }
            vColorDot.background = dot

            val cardAdapter = CardAdapter(
                cards       = col.cards,
                onEdit      = { cardIdx -> onCardEdit(holder.bindingAdapterPosition, cardIdx) },
                onDelete    = { cardIdx -> onCardDelete(holder.bindingAdapterPosition, cardIdx) },
                onMoveNext  = { cardIdx -> onCardMoveNext(holder.bindingAdapterPosition, cardIdx) },
                onReorder   = onReorder
            )
            rvCards.layoutManager = LinearLayoutManager(holder.itemView.context)
            rvCards.adapter       = cardAdapter
            rvCards.isNestedScrollingEnabled = true
            cardAdapter.attachDragHelper(rvCards)

            btnAddCard.setOnClickListener     { onCardAdd(holder.bindingAdapterPosition) }
            btnDeleteColumn.setOnClickListener { onColumnDelete(holder.bindingAdapterPosition) }
        }
    }

    override fun getItemCount() = columns.size
}
