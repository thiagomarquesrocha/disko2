package com.tja.disko2.features.util

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.tja.disko2.features.listplace.MainActivity
import com.tja.disko2.features.listplace.PlaceViewModel

class MyViewModelFactory(
    private val mainActivity: Activity,
    private val mApplication: Application) :
    NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PlaceViewModel::class.java) {
            PlaceViewModel(mainActivity, mApplication) as T
        } else {
            super.create(modelClass)
        }
    }
}