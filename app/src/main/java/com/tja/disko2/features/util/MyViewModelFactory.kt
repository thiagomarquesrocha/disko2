package com.tja.disko2.features.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.tja.disko2.features.listplace.PlaceViewModel

class MyViewModelFactory(private val mApplication: Application) :
    NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PlaceViewModel::class.java) {
            PlaceViewModel(mApplication) as T
        } else {
            super.create(modelClass)
        }
    }
}