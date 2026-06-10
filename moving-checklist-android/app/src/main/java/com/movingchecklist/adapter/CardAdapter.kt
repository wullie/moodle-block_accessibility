package com.movingchecklist.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.movingchecklist.databinding.ItemCardBinding
import com.movingchecklist.model.Card
import java.util.Collections

class CardAdapter(
    val cards: MutableList<Card>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
    private val onMoveNext: (Int) -> Unit,
    private val onReorder: () -> Unit
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        with(holder.binding) {
            tvCardText.text = card.text

            tvPriorityBadge.text = card.priority.label
            tvPriorityBadge.setTextColor(Color.parseColor(card.priority.textColorHex))
            val badgeBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor(card.priority.bgColorHex))
            }
            tvPriorityBadge.background = badgeBg

            priorityBar.setBackgroundColor(Color.parseColor(card.priority.accentColorHex))

            btnEdit.setOnClickListener     { onEdit(holder.bindingAdapterPosition) }
            btnDelete.setOnClickListener   { onDelete(holder.bindingAdapterPosition) }
            btnMoveNext.setOnClickListener { onMoveNext(holder.bindingAdapterPosition) }
        }
    }

    override fun getItemCount() = cards.size

    fun attachDragHelper(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                rv: RecyclerView,
                src: RecyclerView.ViewHolder,
                dst: RecyclerView.ViewHolder
            ): Boolean {
                val from = src.bindingAdapterPosition
                val to   = dst.bindingAdapterPosition
                if (from < 0 || to < 0) return false
                Collections.swap(cards, from, to)
                notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(rv: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(rv, viewHolder)
                onReorder()
            }
        }).attachToRecyclerView(recyclerView)
    }
}
