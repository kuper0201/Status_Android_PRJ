package com.example.test

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.test.databinding.ActivityMainBinding
import com.example.test.db.ItemData
import com.example.test.db.SharedViewModelFactory
import com.example.test.home.HomeFragment
import com.example.test.item.ItemFragment
import com.example.test.setting.SettingFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this, SharedViewModelFactory(applicationContext)).get(SharedViewModel::class.java)

        // 초기 실행시 기본 데이터 설정
        val pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if(!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()

            val firstItems = ArrayList<ItemData>()
            firstItems.add(ItemData("공 종", true, 0))
            firstItems.add(ItemData("날 짜", false, 1))
            sharedViewModel.insertMultiData(firstItems)
        } else {
            sharedViewModel.getAllData()
        }

        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, HomeFragment(sharedViewModel)).commit()

        binding.navigationView.setOnItemSelectedListener { item ->
            val fragTransaction = supportFragmentManager.beginTransaction()
            with(fragTransaction) {
                when(item.itemId) {
                    R.id.homeFragment -> fragTransaction.replace(R.id.mainFrameLayout, HomeFragment(sharedViewModel))
                    R.id.itemFragment -> fragTransaction.replace(R.id.mainFrameLayout, ItemFragment(sharedViewModel))
                    R.id.settingFragment -> fragTransaction.replace(R.id.mainFrameLayout, SettingFragment())
                }
//                commitAllowingStateLoss()
                commit()
                true
            }
        }
    }
}