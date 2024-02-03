package com.example.test.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.test.R
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.databinding.FragmentItemBinding
import com.example.test.databinding.FragmentLargeImageBinding

class LargeImageFragment : DialogFragment {
    private var mBinding: FragmentLargeImageBinding? = null
    private val binding get() = mBinding!!
    private val imageData: Bitmap

    constructor(imageData: Bitmap) {
        this.imageData = imageData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentLargeImageBinding.inflate(inflater, container, false)

        binding.largeImage.setImageBitmap(imageData)

        return binding.root
    }
}