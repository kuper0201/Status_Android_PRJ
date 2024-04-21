package com.example.test

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.databinding.ActivityMainBinding
import com.example.test.db.ItemData
import com.example.test.home.HomeFragment
import com.example.test.item.ItemFragment
import com.example.test.setting.SettingFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var homeFrag: HomeFragment? = null
    private var itemFrag: ItemFragment? = null
    private var settingFrag: SettingFragment? = null

    init {
        homeFrag = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        sharedViewModel = ViewModelProvider(this, SharedViewModelFactory(applicationContext)).get(SharedViewModel::class.java)

        // 초기 실행시 기본 데이터 설정
        val pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()

            val firstItems = ArrayList<ItemData>()
            firstItems.add(ItemData("공 종", true, 0))
            firstItems.add(ItemData("날 짜", false, 1))
//            sharedViewModel.insertMultiData(firstItems)
        } else {
//            sharedViewModel.getAllData()
        }

        supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, homeFrag!!).commit()

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    if (homeFrag == null) {
                        homeFrag = HomeFragment()
                        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, homeFrag!!).commit()
                    }

                    if (itemFrag != null) supportFragmentManager.beginTransaction().hide(itemFrag!!).commit()
                    if (settingFrag != null) supportFragmentManager.beginTransaction().hide(settingFrag!!).commit()
                    if (homeFrag != null) supportFragmentManager.beginTransaction().show(homeFrag!!).commit()
                    return@setOnItemSelectedListener true
                }

                R.id.itemFragment -> {
                    if (itemFrag == null) {
                        itemFrag = ItemFragment()
                        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, itemFrag!!).commit()
                    }

                    if (settingFrag != null) supportFragmentManager.beginTransaction().hide(settingFrag!!).commit()
                    if (homeFrag != null) supportFragmentManager.beginTransaction().hide(homeFrag!!).commit()
                    if (itemFrag != null) supportFragmentManager.beginTransaction().show(itemFrag!!).commit()
                    return@setOnItemSelectedListener true
                }

                R.id.settingFragment -> {
                    if (settingFrag == null) {
                        settingFrag = SettingFragment()
                        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, settingFrag!!).commit()
                    }

                    if (homeFrag != null) supportFragmentManager.beginTransaction().hide(homeFrag!!).commit()
                    if (itemFrag != null) supportFragmentManager.beginTransaction().hide(itemFrag!!).commit()
                    if (settingFrag != null) supportFragmentManager.beginTransaction().show(settingFrag!!).commit()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }
}