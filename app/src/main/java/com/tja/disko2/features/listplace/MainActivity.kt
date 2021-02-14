package com.tja.disko2.features.listplace

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tja.disko2.R
import com.tja.disko2.features.aboutapp.AboutAppActivity
import com.tja.disko2.features.aboutplace.AboutPlaceActivity
import com.tja.disko2.features.util.SectionsPagerAdapter
import com.tja.disko2.features.util.Utils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_about -> {
                startActivity(Intent(this,AboutAppActivity::class.java))
            }
            R.id.action_share  ->{
                val contentToShare =
                    "Baixe DiskO2 na Google Play \n https://play.google.com/store/apps/details?id=com.tja.disko2.\nLocalize os estabelecimentos que trabalham com oxigênio em Manaus."
                Utils.sendContentToShare(this, contentToShare);
            }
        }
        return super.onOptionsItemSelected(item)
    }
}