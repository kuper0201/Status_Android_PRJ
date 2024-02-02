package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.test.databinding.ActivityMainBinding
import com.example.test.home.HomeFragment
import com.example.test.item.ItemFragment
import com.example.test.setting.SettingFragment

private const val TAG_SETTING = "setting_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ITEM = "item_fragment"

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.itemFragment -> setFragment(TAG_ITEM, ItemFragment())
                R.id.settingFragment -> setFragment(TAG_SETTING, SettingFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val item = manager.findFragmentByTag(TAG_ITEM)
        val setting = manager.findFragmentByTag(TAG_SETTING)

        with(fragTransaction) {
            if (home != null) hide(home)
            if (item != null) hide(item)
            if (setting != null) hide(setting)

            if (tag == TAG_HOME) {
                if (home != null) show(home)
            } else if (tag == TAG_SETTING) {
                if (setting != null) show(setting)
            } else if(tag == TAG_ITEM) {
                if (item != null) show(item)
            }

            commitAllowingStateLoss()
        }
    }
}