package com.example.test.item

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.fragment.app.DialogFragment
import com.example.test.R
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.db.ItemData
import kotlinx.coroutines.delay

class ItemAddFragment(val position: Int, val itemAddInterface: ItemAddInterface) : DialogFragment() {
    private var mBinding: FragmentItemAddBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentItemAddBinding.inflate(inflater, container, false)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val type = (binding.radioType.checkedRadioButtonId == R.id.textType)
            if (binding.nameEditText.text == null || binding.nameEditText.text.toString().equals("")) {
                Toast.makeText(requireContext(), R.string.blank_item_name, Toast.LENGTH_SHORT).show()
            } else {
                val item = ItemData(
                    itemName = binding.nameEditText.text.toString(),
                    itemType = type,
                    itemOrder = position
                )

                this.itemAddInterface.insertCallback(item)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

interface ItemAddInterface {
    fun insertCallback(data: ItemData)
}