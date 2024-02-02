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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.db.ItemData
import com.example.test.db.LocalDatabase
import com.example.test.utils.ProgressDialogFragment
import kotlinx.coroutines.*

class HomeFragment : Fragment(), DrawInter {
    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!

    private lateinit var db: LocalDatabase
    private lateinit var listAdapter: HomeItemListAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = LocalDatabase.getInstance(requireContext())!!
        listAdapter = HomeItemListAdapter(db, this)
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
                    viewPagerAdapter.imgList = lst
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }
        }

        val pref = requireActivity().getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if(!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()

            val firstItems = ArrayList<ItemData>()
            firstItems.add(ItemData("공 종", true, 0))
            firstItems.add(ItemData("날 짜", false, 1))

            CoroutineScope(Dispatchers.Main).launch {
                val ret = CoroutineScope(Dispatchers.IO).async {
                    for(i in firstItems) {
                        db.itemDAO().insertData(i)
                    }
                }

                ret.await()
                listAdapter.getAllData()
            }

//            listAdapter.itemList = firstItems
//            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            listAdapter.getAllData()
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

        return binding.root
    }

    override fun reDraw() {
        val il = listAdapter.itemList
        val vl = listAdapter.valueList

        val tmp_list = il.zip(vl) { a, b ->
            a.itemName to b
        }
        val res = ArrayList<Pair<String, String>>(tmp_list)

        viewPagerAdapter.itemList = res
        viewPagerAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}