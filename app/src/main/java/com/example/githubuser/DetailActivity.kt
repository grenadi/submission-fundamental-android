package com.example.githubuser

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.AVATAR
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.COMPANY
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.FAVOURITE
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.FOLLOWERS
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.FOLLOWING
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.LOCATION
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.NAME
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.example.githubuser.db.FavoriteHelper
import com.example.githubuser.model.Favorite
import com.example.githubuser.model.User
import com.example.githubuser.viewModel.SectionPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_USER = "extra_user"
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"

    }

    private var isFavourite = false
    private lateinit var gitHelper: FavoriteHelper
    private var favourites: Favorite? = null
    private lateinit var imgAvatar: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        viewPagerConfig()
        fab_fav.setOnClickListener(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_views)
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_home -> {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    true
                }

                R.id.nav_fav -> {
                    val i = Intent(this, FavouriteActivity::class.java)
                    startActivity(i)
                    true
                }

                R.id.nav_setting -> {
                    val i = Intent(this, SettingActivity::class.java)
                    startActivity(i)
                    true
                }
                else -> true
            }
        }

        gitHelper = FavoriteHelper.getInstance(applicationContext)
        gitHelper.open()

        favourites = intent.getParcelableExtra(EXTRA_NOTE)
        if (favourites != null){
            setDataObject()
            isFavourite = true
            val checked: Int = R.drawable.ic_baseline_favorite_red_24
            fab_fav.setImageResource(checked)
        } else {
            setData()
        }

    }

    private fun setDataObject() {
        val favUser = intent.getParcelableExtra<Favorite>(EXTRA_NOTE) as Favorite
        favUser.username?.let { setActionBarTitle(it) }
        tv_username.text = favUser.username
        tv_name.text = favUser.name
        Glide.with(this)
                .load(favUser.photo)
                .into(img_detail)
        tv_company.text = favUser.company
        tv_location.text = favUser.location
        tv_repository.text = favUser.repository
        tv_followers.text = favUser.followers
        tv_following.text = favUser.following
        imgAvatar = favUser.photo.toString()
    }

    private fun setData(){
        val dataUser = intent.getParcelableExtra<User>(EXTRA_USER) as User
        dataUser.username?.let { setActionBarTitle(it) }
        Glide.with(this)
                .load(dataUser.photo)
                .into(img_detail)
        tv_name.text = dataUser.name
        tv_username.text = dataUser.username
        tv_location.text = dataUser.location
        tv_company.text = dataUser.company
        tv_repository.text = dataUser.repository
        tv_followers.text = dataUser.followers
        tv_following.text = dataUser.following
        imgAvatar = dataUser.photo.toString()
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

    override fun onClick(view: View) {
        val checked: Int = R.drawable.ic_baseline_favorite_red_24
        val unChecked: Int = R.drawable.ic_baseline_favorite_border_red_24
        if (view.id == R.id.fab_fav){
            if (isFavourite){
                gitHelper.deleteById(favourites?.username.toString())
                Toast.makeText(this, "Deleted from favourite list", Toast.LENGTH_SHORT).show()
                fab_fav.setImageResource(unChecked)
                isFavourite = false
            } else {
                val dataUsername = tv_username.text.toString()
                val dataName = tv_name.text.toString()
                val dataAvatar = imgAvatar
                val dataCompany = tv_company.text.toString()
                val dataLocation = tv_location.text.toString()
                val dataRepo = tv_repository.text.toString()
                val dataFollowers = tv_followers.text.toString()
                val dataFollowing = tv_following.text.toString()
                val dataFavourite = "1"

                val values = ContentValues()
                values.put(USERNAME, dataUsername)
                values.put(NAME, dataName)
                values.put(AVATAR, dataAvatar)
                values.put(COMPANY, dataCompany)
                values.put(LOCATION, dataLocation)
                values.put(REPOSITORY, dataRepo)
                values.put(FOLLOWERS, dataFollowers)
                values.put(FOLLOWING, dataFollowing)
                values.put(FAVOURITE, dataFavourite)

                isFavourite = true
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, "Added to favourite list", Toast.LENGTH_SHORT).show()
                fab_fav.setImageResource(checked)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gitHelper.close()
    }
}