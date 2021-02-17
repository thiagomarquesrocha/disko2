package com.tja.disko2.features.aboutplace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.tja.disko2.R
import com.tja.disko2.domain.PlaceO2
import com.tja.disko2.features.listplace.PlaceViewModel
import com.tja.disko2.features.util.MyViewModelFactory
import com.tja.disko2.features.util.Utils

class AboutPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var cardCall: CardView
    private lateinit var cardWpp: CardView
    private lateinit var viewModel: PlaceViewModel
    private lateinit var placeO2: PlaceO2
    private lateinit var ivFavorite: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvType: TextView
    private lateinit var tvTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_place)

        // ViewModel
        viewModel = ViewModelProvider(this, MyViewModelFactory(this, application)).get(
            PlaceViewModel::class.java
        )

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        placeO2 = intent.getSerializableExtra(key) as PlaceO2

        tvTitle = findViewById(R.id.tv_title)
        tvAddress = findViewById(R.id.tv_address)
        tvType = findViewById(R.id.tv_type)
        ivFavorite = findViewById(R.id.iv_favorite)
        tvTime = findViewById(R.id.tv_time)
        cardWpp = findViewById(R.id.card_wpp)
        cardCall = findViewById(R.id.card_call)
        cardWpp.setOnClickListener(this)
        cardCall.setOnClickListener(this)
        ivFavorite.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        if (placeO2 == null) {
            finish()
        }

        if (placeO2.favorite == 1) {
            ivFavorite.setImageResource(R.drawable.icon_heart_red)
        } else {
            ivFavorite.setImageResource(R.drawable.icon_heart_outline)
        }

        tvTitle.text = placeO2.name
        // Adress
        Utils.displayAdress(placeO2, tvAddress)
        // Time
        Utils.displayTime(placeO2, tvTime)
        // Status
        Utils.displayPlaceStatus(placeO2, tvType)

        // Disable whatsapp button
        Utils.displayButton(placeO2.whatsapp, cardWpp)
        // Disable call button
        Utils.displayButton(placeO2.call, cardCall)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about_place, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_share -> {

                val contentToShare =
                    "DiskO2 indica: ${placeO2.name} \n ${placeO2.address} \n ${placeO2.phone} "
                Utils.sendContentToShare(this, contentToShare);
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val key = "place02"

        @JvmStatic
        fun openAboutPlaceActivity(context: Context, placeO2: PlaceO2) {
            val intent = Intent(context, AboutPlaceActivity::class.java)
            intent.putExtra(key, placeO2)
            context.startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_favorite -> {
                viewModel.favorite(placeO2)
                if (placeO2.favorite == 1) {
                    placeO2.favorite = 0
                    ivFavorite.setImageResource(R.drawable.icon_heart_outline)
                } else {
                    placeO2.favorite = 1
                    ivFavorite.setImageResource(R.drawable.icon_heart_red)
                }
            }
            R.id.card_wpp -> {
                viewModel.intentWpp(placeO2)
            }
            R.id.card_call -> {
                viewModel.intentCall(placeO2)
            }
        }
    }

}