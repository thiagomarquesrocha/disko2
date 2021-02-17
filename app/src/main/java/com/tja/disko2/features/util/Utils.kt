package com.tja.disko2.features.util

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.tja.disko2.R
import com.tja.disko2.domain.PlaceO2


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
    
    @JvmStatic
    fun displayPlaceStatus(placeO2: PlaceO2, tvTitle: TextView){
        when (placeO2.type) {
            1 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_1)
            }
            2 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_2)
            }
            3 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_3)
            }
            4 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_4)
            }
            5 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_5)
            }
            6 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_6)
            }
            7 -> {
                tvTitle.text = tvTitle.context.getString(R.string.place_type_7)
            }
            else ->{
                tvTitle.text = tvTitle.context.getString(R.string.place_type_undefined)
            }
        }
    }

    @JvmStatic
    fun displayTime(placeO2: PlaceO2, itemView: TextView){
        itemView.text = if (placeO2.time.isEmpty()) itemView.context.getString(R.string.place_not_time) else placeO2.time
    }

    @JvmStatic
    fun displayAdress(placeO2: PlaceO2, itemView: TextView){
        itemView.text = if (placeO2.address.isEmpty()) itemView.context.getString(R.string.place_not_address) else placeO2.address
    }

    @JvmStatic
    fun displayButton(value: String, cardCall: CardView) {
        if(value.isEmpty()){
            cardCall.setEnabled(false)
            cardCall.setAlpha(0.4F)
        }else{
            cardCall.setEnabled(true)
            cardCall.setAlpha(1.0F)
        }
    }


}
