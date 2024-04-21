package com.example.test.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.test.R
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.db.ItemData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ItemAddFragment(val position: Int, val inter: ItemAddInterface) : DialogFragment() {
    interface ItemAddInterface {
        suspend fun addData(item: ItemData): Boolean
    }

    private var mBinding: FragmentItemAddBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentItemAddBinding.inflate(inflater, container, false)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.nameEditText.text
            val type = (binding.radioType.checkedRadioButtonId == R.id.textType)
            val item = ItemData(name.toString(), type, position)

            if (name.toString().equals("")) {
                Toast.makeText(requireContext(), R.string.blank_item_name, Toast.LENGTH_SHORT).show()
            }

            CoroutineScope(Dispatchers.IO).launch {
                val cnt = inter.addData(item)

                CoroutineScope(Dispatchers.Main).launch {
                    if(!cnt) {
                        Toast.makeText(requireContext(), "${item.itemName} 항목이 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        dismiss()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}