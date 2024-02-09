package com.example.test.home

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.test.SharedViewModel
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import com.example.test.db.SharedViewModelFactory
import com.example.test.utils.ProgressDialogFragment
import kotlinx.coroutines.*

class HomeFragment(val sharedViewModel: SharedViewModel) : Fragment() {
    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!

    private lateinit var listAdapter: HomeItemListAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listAdapter = HomeItemListAdapter(sharedViewModel)
        viewPagerAdapter = ViewPagerAdapter(parentFragmentManager, sharedViewModel)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val itemCount = it.data?.clipData?.itemCount!!
                var lst = ArrayList<Bitmap>()

                val dlg = ProgressDialogFragment(itemCount, "이미지를 불러오는 중입니다...")
                dlg.show(requireActivity().supportFragmentManager, dlg.tag)
                CoroutineScope(Dispatchers.Main).launch {
                    val ret = async(Dispatchers.IO) {
                        for (i in 0 until itemCount) {
                            val item = it.data?.clipData?.getItemAt(i)?.uri!!
                            val bm = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, item))
                            lst.add(bm)

                            dlg.addProgress()
                        }

                        dlg.dismiss()
                    }

                    ret.await()
                    sharedViewModel.updateImageList(lst)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val list = binding.homeItemList
        list.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        val pagerView = binding.viewPager
        pagerView.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            adapter = viewPagerAdapter
            setPageTransformer(MarginPageTransformer(50))
            setPadding(100, 0, 100, 0)
        }

        binding.imageBtn.setOnClickListener {
            val intent = Intent()
            intent.apply {
                setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                setType("image/*")
                setAction(Intent.ACTION_GET_CONTENT)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }

            activityResultLauncher.launch(intent)
        }

        binding.saveAllImgBtn.setOnClickListener {
            viewPagerAdapter.save(requireContext())
        }

        sharedViewModel.repo.getItemList().observe(viewLifecycleOwner, Observer {
            listAdapter.itemList = it
            listAdapter.notifyDataSetChanged()

            viewPagerAdapter.itemList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        sharedViewModel.repo.getStringList().observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.stringList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        sharedViewModel.repo.getImgList().observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.imgList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}