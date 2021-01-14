package com.example.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.model.User
import com.example.githubuser.viewModel.UserAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val list = ArrayList<User>()
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lv_list.setHasFixedSize(true)

        getUser()
        searchUser()

        val navView: BottomNavigationView = findViewById(R.id.nav_views)
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId){
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

    }

    private fun searchUser() {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()){
                    return true
                } else {
                    list.clear()
                    getUserSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getUser(){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d948aa4b3ed82bf58a798691d612a8325bfaab2a")
        val url = "https://api.github.com/users"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val username: String = jsonObject.getString("login")
                        getUserDetail(username)
                    }
                } catch (e: Exception){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserSearch(id: String){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d948aa4b3ed82bf58a798691d612a8325bfaab2a")
        val url = "https://api.github.com/search/users?q=$id"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONObject(result)
                    val item = jsonArray.getJSONArray("items")
                    for (i in 0 until item.length()){
                        val jsonObject = item.getJSONObject(i)
                        val username = jsonObject.getString("login")
                        getUserDetail(username)
                    }
                } catch (e: Exception){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserDetail(id: String){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d948aa4b3ed82bf58a798691d612a8325bfaab2a")
        val url = "https://api.github.com/users/$id"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val username = jsonObject.getString("login").toString()
                    val name = jsonObject.getString("name").toString()
                    val avatar = jsonObject.getString("avatar_url").toString()
                    val company = jsonObject.getString("company").toString()
                    val location = jsonObject.getString("location").toString()
                    val repository = jsonObject.getString("public_repos")
                    val followers = jsonObject.getString("followers")
                    val following = jsonObject.getString("following")
                    list.add(
                            User(
                                    avatar,
                                    name,
                                    username,
                                    location,
                                    repository,
                                    company,
                                    followers,
                                    following
                            )
                    )
                    showRecyclerList()
                } catch (e: Exception){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showRecyclerList(){
        lv_list.layoutManager = LinearLayoutManager(lv_list.context)
        val listUserAdapter = UserAdapter(list)
        lv_list.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: User) {
                val detailIntent = Intent(this@MainActivity, DetailActivity::class.java)
                detailIntent.putExtra(DetailActivity.EXTRA_USER, data)
                startActivity(detailIntent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        list.clear()
        search.setQuery("", false)
    }
}