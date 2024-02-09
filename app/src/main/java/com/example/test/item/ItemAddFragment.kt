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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import com.example.test.SharedViewModel
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.db.ItemData
import com.example.test.db.SharedViewModelFactory
import kotlin.collections.ArrayList

class ItemAddFragment(val position: Int, val sharedViewModel: SharedViewModel) : DialogFragment() {
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
            } else if(sharedViewModel.repo.getItemList().value!!.contains(item)) {
                Toast.makeText(requireContext(), "${item.itemName} 항목이 이미 존재합니다.", Toast.LENGTH_SHORT).show()
            } else {
                sharedViewModel.insertData(item)
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