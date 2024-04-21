package com.example.test.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.FragmentItemBinding
import com.example.test.db.ItemData
import java.util.ArrayList

class ItemFragment : Fragment(), ItemListAdapter.ItemListInterface, ItemAddFragment.ItemAddInterface {
    private var mBinding: FragmentItemBinding? = null
    private val binding get() = mBinding!!

    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var viewModel: ItemViewModel
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ItemViewModel.Factory(requireActivity().application)).get(ItemViewModel::class.java)
        itemListAdapter = ItemListAdapter(ArrayList(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentItemBinding.inflate(inflater, container, false)

        viewModel.getItemList().observe(viewLifecycleOwner, Observer {
            itemListAdapter.itemList = it
            itemListAdapter.notifyDataSetChanged()
        })

        touchHelper = ItemTouchHelper(ItemTouchHelperCallback(itemListAdapter))
        touchHelper.attachToRecyclerView(binding.itemList)

        binding.itemList.apply {
            adapter = itemListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.addButton.setOnClickListener {
            val itemAddFrag = ItemAddFragment(itemListAdapter.itemCount, this)
            itemAddFrag.show(requireActivity().supportFragmentManager, itemAddFrag.tag)
        }

        return binding.root
    }

    // When fragment down, update reordered data to DB
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden) {
            viewModel.reorderItem(itemListAdapter.itemList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun deleteItem(item: ItemData) {
        viewModel.deleteItemData(item)
    }

    // Implementation of Interface
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    override suspend fun addData(item: ItemData): Boolean {
        return viewModel.insertData(item)
    }
}