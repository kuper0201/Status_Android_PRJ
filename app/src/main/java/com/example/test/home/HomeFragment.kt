package com.example.test.home

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.utils.ProgressDialogFragment
import kotlinx.coroutines.*

class HomeFragment : Fragment(), HomeItemListAdapter.HomeItemListInterface {
    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!

    private lateinit var homeItemListAdapter: HomeItemListAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, HomeViewModel.Factory(requireActivity().application)).get(HomeViewModel::class.java)

        homeItemListAdapter = HomeItemListAdapter(this)
        viewPagerAdapter = ViewPagerAdapter(parentFragmentManager)
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
                    viewModel.setImages(lst)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.getItemList().observe(viewLifecycleOwner, Observer {
            homeItemListAdapter.itemList = it
            homeItemListAdapter.notifyDataSetChanged()

            viewPagerAdapter.itemList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        viewModel.getStringMap().observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.stringList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        viewModel.getImages().observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.imgList = it
            viewPagerAdapter.notifyDataSetChanged()
        })

        binding.homeItemList.apply {
            adapter = homeItemListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.viewPager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            adapter = viewPagerAdapter
            setPageTransformer(MarginPageTransformer(50))
            setPadding(100, 0, 100, 0)
        }

        binding.imageBtn.setOnClickListener {
            val intent = Intent().apply {
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun updateStringData(origin: String, to: String) {
        viewModel.updateStr(origin, to)
    }
}