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
import com.example.test.SharedViewModel
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
    private var sharedViewModel: SharedViewModel

    constructor(sharedViewModel: SharedViewModel) {
        this.itemList = ArrayList()
        this.sharedViewModel = sharedViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        mContext = view.context

        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeItemListAdapter.BoardViewHolder, position: Int) {
        val strList = sharedViewModel.repo.getStringList().value!!

        val s = itemList[position].itemName
        holder.name.text = itemList[position].itemName
        holder.editContent.setText(strList.get(itemList[position].itemName))
        val tw = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                sharedViewModel.updateStringData(s, p0.toString())
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
}