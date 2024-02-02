package com.example.test.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.FragmentItemBinding
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import java.util.ArrayList

class ItemFragment : Fragment(), ItemAddInterface {
    private var mBinding: FragmentItemBinding? = null
    private val binding get() = mBinding!!

    private lateinit var db: LocalDatabase
    private lateinit var adapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = LocalDatabase.getInstance(requireContext())!!
        adapter = ItemListAdapter(ArrayList(), db)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentItemBinding.inflate(inflater, container, false)

        val list = binding.itemList
        adapter.getAllData()

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
            val itemAddFrag = ItemAddFragment(adapter.itemCount, this)
            itemAddFrag.show(requireActivity().supportFragmentManager, itemAddFrag.tag)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun insertCallback(data: ItemData) {
        adapter.insertData(data)
    }
}