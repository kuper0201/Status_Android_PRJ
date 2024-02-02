package com.example.test.home

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import com.example.test.item.ItemListAdapter
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class HomeItemListAdapter : RecyclerView.Adapter<HomeItemListAdapter.BoardViewHolder> {
    private lateinit var mContext: Context
    var itemList: ArrayList<ItemData>
    var valueList: ArrayList<String>
    private var db: LocalDatabase
    private var inter: DrawInter

    constructor(db: LocalDatabase, inter: DrawInter) {
        this.itemList = ArrayList()
        this.valueList = ArrayList()
        this.db = db
        this.inter = inter

        getAllData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        mContext = view.context

        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeItemListAdapter.BoardViewHolder, position: Int) {
        holder.name.text = itemList[position].itemName
        val tw = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                valueList.set(holder.adapterPosition, p0.toString())
                inter.reDraw()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }

        holder.editContent.addTextChangedListener(tw)

        holder.moreAction.setOnClickListener {
//            holder.editContent.setText(itemList[position].itemName)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(itemList[position].itemType) return 1
        return 0
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.nameText)
        val editContent = itemView.findViewById<EditText>(R.id.edit_content)
        val moreAction = itemView.findViewById<ImageButton>(R.id.more_action)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getAllData() {
        CoroutineScope(Dispatchers.Main).launch {
            val items = withContext(Dispatchers.IO) {
                db.itemDAO().getAllItemData()
            }
            itemList = ArrayList(items)
            for(i in 0 until itemList.count()) {
                valueList.add("")
            }
            notifyDataSetChanged()
            inter.reDraw()
        }
    }
}

interface DrawInter {
    fun reDraw()
}