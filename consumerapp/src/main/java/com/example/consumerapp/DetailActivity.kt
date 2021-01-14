package com.example.consumerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.consumerapp.model.Favorite
import com.example.consumerapp.viewModel.SectionPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(){

    companion object {
        const val EXTRA_USER = "extra_user"
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"

    }

    private lateinit var imgAvatar: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        viewPagerConfig()
        setDataObject()


        val navView: BottomNavigationView = findViewById(R.id.nav_views)
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_setting -> {
                    val i = Intent(this, SettingActivity::class.java)
                    startActivity(i)
                    true
                }
                else -> true
            }
        }
    }

    private fun setDataObject() {
        val favUser = intent.getParcelableExtra<Favorite>(EXTRA_NOTE) as Favorite
        favUser.username?.let { setActionBarTitle(it) }
        Glide.with(this)
                .load(favUser.photo)
                .into(img_detail)
        tv_name.text = favUser.name
        tv_username.text = favUser.username
        tv_location.text = favUser.location
        tv_company.text = favUser.company
        tv_repository.text = favUser.repository
        tv_followers.text = favUser.followers
        tv_following.text = favUser.following
        imgAvatar = favUser.photo.toString()
    }

    private fun setActionBarTitle(title: String){
        if (supportActionBar != null){
            this.title = title
        }
    }

    private fun viewPagerConfig() {
        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionPagerAdapter
        tabs.setupWithViewPager(view_pager)

        supportActionBar?.elevation = 0f
    }
}