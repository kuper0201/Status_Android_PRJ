package com.example.test.home

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View.OnTouchListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.example.test.R
import com.example.test.databinding.FragmentItemAddBinding
import com.example.test.databinding.FragmentItemBinding
import com.example.test.databinding.FragmentLargeImageBinding

class LargeImageFragment : DialogFragment {
    private var mBinding: FragmentLargeImageBinding? = null
    private val binding get() = mBinding!!
    private val imageData: Bitmap

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var scaleFactor = 1.0f

    constructor(imageData: Bitmap) {
        this.imageData = imageData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentLargeImageBinding.inflate(inflater, container, false)

        mScaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())

        binding.largeImage.setImageBitmap(imageData)
        binding.largeImage.setOnTouchListener { v, event ->
            mScaleGestureDetector!!.onTouchEvent(event)
        }

        return binding.root
    }

    inner class ScaleListener : OnScaleGestureListener, OnTouchListener {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor

            // 최소 0.5, 최대 2배
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 2.0f))

            // 이미지에 적용
            binding.largeImage.scaleX = scaleFactor
            binding.largeImage.scaleY = scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            TODO("Not yet implemented")
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            TODO("Not yet implemented")
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            dismiss()
            return true
        }
    }
}