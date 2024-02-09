package com.example.test.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.SharedViewModel
import com.example.test.databinding.FragmentItemBinding
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import com.example.test.db.SharedViewModelFactory
import java.util.ArrayList

class ItemFragment(val sharedViewModel: SharedViewModel) : Fragment() {
    private var mBinding: FragmentItemBinding? = null
    private val binding get() = mBinding!!

    private lateinit var adapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ItemListAdapter(ArrayList(), sharedViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentItemBinding.inflate(inflater, container, false)

        val list = binding.itemList

        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(list)
        list.adapter = adapter
        adapter.startDrag(object : ItemListAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        })
        list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.addButton.setOnClickListener {
            val itemAddFrag = ItemAddFragment(adapter.itemCount, sharedViewModel)
            itemAddFrag.show(requireActivity().supportFragmentManager, itemAddFrag.tag)
        }

        sharedViewModel.repo.getItemList().observe(viewLifecycleOwner, Observer {
            adapter.itemList = it
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.reorderItem(adapter.itemList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}