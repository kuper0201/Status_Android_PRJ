package com.example.test.home

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.HomeListItemBinding
import com.example.test.db.ItemData
import kotlin.collections.ArrayList

class HomeItemListAdapter(val inter: HomeItemListInterface) : RecyclerView.Adapter<HomeItemListAdapter.BoardViewHolder>() {
    interface HomeItemListInterface {
        fun updateStringData(origin: String, to: String)
    }

    var itemList: List<ItemData>
    var strMap: HashMap<String, String>

    init {
        itemList = ArrayList()
        strMap = HashMap()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val listItemBinding = HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: HomeItemListAdapter.BoardViewHolder, position: Int) {
        val itName = itemList[position].itemName
        holder.bind(itName, strMap.getOrDefault(itName, ""))

        holder.editContent.setText(strMap.get(itName))
        val tw = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                inter.updateStringData(itName, p0.toString())
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

    inner class BoardViewHolder(val binding: HomeListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: String, itemContent: String) {
            binding.itemName = itemName
            binding.itemContent = itemContent
        }

        val name = binding.nameText
        val editContent = binding.editContent
        val moreAction = binding.moreAction
    }
}