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
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class ItemListAdapter(var itemList: ArrayList<ItemData>, var db: LocalDatabase) : RecyclerView.Adapter<ItemListAdapter.BoardViewHolder>(), ItemTouchHelperCallback.OnItemMoveListener {
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
        val data = itemList.get(position)
        itemList.remove(data)
        notifyItemRemoved(position)

        CoroutineScope(Dispatchers.IO).launch {
            db.itemDAO().deleteAllItemData()
            for((idx, item) in itemList.withIndex()) {
                var reorderedItem = ItemData(itemName = item.itemName, itemType = item.itemType, itemOrder = idx)
                db.itemDAO().insertData(reorderedItem)
            }
        }
    }

    override fun afterDrag() {
        CoroutineScope(Dispatchers.IO).launch {
            db.itemDAO().deleteAllItemData()
            for ((idx, item) in itemList.withIndex()) {
                var reorderedItem = ItemData(itemName = item.itemName, itemType = item.itemType, itemOrder = idx)
                db.itemDAO().insertData(reorderedItem)
            }
        }
    }

    fun getAllData() {
        CoroutineScope(Dispatchers.Main).launch {
            var items = withContext(Dispatchers.IO) {
                db.itemDAO().getAllItemData()
            }

            itemList = ArrayList(items)
            notifyDataSetChanged()
        }
    }

    fun insertData(data: ItemData) {
        if(itemList.contains(data)) {
            Toast.makeText(mContext, "${data.itemName} 항목이 이미 존재합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).launch {
                db.itemDAO().insertData(data)
            }

            itemList.add(data)
            notifyItemInserted(itemList.size - 1)
        }
    }
}