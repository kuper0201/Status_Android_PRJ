package com.example.test.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.AddListItemBinding
import com.example.test.db.ItemData
import kotlinx.coroutines.*
import java.util.*

class ItemListAdapter(var itemList: List<ItemData>, val inter: ItemListInterface) : RecyclerView.Adapter<ItemListAdapter.BoardViewHolder>(), ItemTouchHelperCallback.OnItemMoveListener {
    interface ItemListInterface {
        fun deleteItem(item: ItemData)
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val listItemBinding = AddListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item.itemName, if(itemList[position].itemType) {"일반"} else {"날짜"})
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class BoardViewHolder(val binding: AddListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(name: String, type: String) {
            binding.itemName = name
            binding.itemType = type
            binding.nameText.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    inter.onStartDrag(this)
                }

                return@setOnTouchListener false
            }
        }
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        inter.deleteItem(itemList[position])
    }
}