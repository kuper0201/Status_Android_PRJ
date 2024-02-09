package com.example.test.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.SharedViewModel
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class ItemListAdapter(var itemList: ArrayList<ItemData>, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ItemListAdapter.BoardViewHolder>(), ItemTouchHelperCallback.OnItemMoveListener {
    private lateinit var startDragListener: OnStartDragListener
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_list_item, parent, false)
        mContext = view.context
        return BoardViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.name.text = itemList[position].itemName
        if(itemList[position].itemType) holder.type.text = "일반"
        else holder.type.text = "날짜"

        holder.name.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startDragListener.onStartDrag(holder)
            }

            return@setOnTouchListener false
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.nameText)
        val type = itemView.findViewById<TextView>(R.id.typeText)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun startDrag(listener: OnStartDragListener) {
        this.startDragListener = listener
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        sharedViewModel.removeItem(position)
    }
}