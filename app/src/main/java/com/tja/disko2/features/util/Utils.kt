package com.tja.disko2.features.util

import android.app.Activity
import android.content.Intent


object Utils {

     @JvmStatic
     fun sendContentToShare(activity: Activity, contentToShare: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, contentToShare)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }
}
