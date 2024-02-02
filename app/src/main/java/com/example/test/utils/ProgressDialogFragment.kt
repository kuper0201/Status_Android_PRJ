package com.example.test.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.example.test.R
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.databinding.FragmentProgressDialogBinding

class ProgressDialogFragment(val maxValue: Int, val text: String) : DialogFragment() {
    private var mBinding: FragmentProgressDialogBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(false)
        mBinding = FragmentProgressDialogBinding.inflate(inflater, container, false)
        binding.progressBar.max = maxValue
        binding.textView.setText(text)
        return binding.root
    }

    fun addProgress() {
        binding.progressBar.setProgress(binding.progressBar.progress + 1)
    }
}