package com.example.test.db

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.SharedViewModel

class SharedViewModelFactory(private val context: Context): ViewModelProvider.Factory{
    //필수
    override fun <T: ViewModel> create(modelClass: Class<T>):T {
        return if(modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            SharedViewModel(context) as T
        } else {
            throw IllegalArgumentException("Unknown viewModel Class")
        }
    }
}