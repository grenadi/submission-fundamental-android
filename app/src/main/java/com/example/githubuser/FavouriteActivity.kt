package com.example.githubuser

import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.example.githubuser.db.Mapping
import com.example.githubuser.model.Favorite
import com.example.githubuser.viewModel.FavouriteAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavouriteActivity : AppCompatActivity() {

    private lateinit var adapter: FavouriteAdapter

    companion object{
        private const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)
        setActionBarTitle()

        recyclerViewFavorite.layoutManager = LinearLayoutManager(this)
        recyclerViewFavorite.setHasFixedSize(true)
        adapter = FavouriteAdapter(this)
        recyclerViewFavorite.adapter = adapter

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler){
            override fun onChange(self: Boolean) {
                //super.onChange(selfChange)
                loadFavAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null){
            loadFavAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Favorite>(EXTRA_STATE)
            if (list != null){
                adapter.listFav = list
            }
        }
    }

    private fun loadFavAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar_Favorite.visibility = View.VISIBLE
            val deferredFav = async(Dispatchers.IO){
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                Mapping.mapCursorToArrayList(cursor)
            }
            val favData = deferredFav.await()
            progressBar_Favorite.visibility = View.INVISIBLE
            if (favData.size > 0){
                adapter.listFav = favData
            } else {
                adapter.listFav = ArrayList()
                showSnackbarMessage()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFav)
    }

    private fun showSnackbarMessage() {
        Snackbar.make(recyclerViewFavorite, "Belum ada data", Snackbar.LENGTH_SHORT).show()
    }

    private fun setActionBarTitle() {
        if (supportActionBar != null){
            supportActionBar?.title = "Favourite Users"
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavAsync()
    }
}