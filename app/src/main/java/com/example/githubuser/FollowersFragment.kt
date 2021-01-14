package com.example.githubuser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.model.Favorite
import com.example.githubuser.model.User
import com.example.githubuser.viewModel.FollowersAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_followers.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception



class FollowersFragment : Fragment() {

    companion object {
        private val TAG = FollowersFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_NOTE = "extra_note"
    }

    private val listUser: ArrayList<User> = ArrayList()
    private lateinit var adapter: FollowersAdapter
    private var favorites: Favorite? = null
    private lateinit var dataFavorite: Favorite
    private lateinit var dataUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listUser.clear()

        recyclerViewFollowers.layoutManager = LinearLayoutManager(activity)
        adapter = FollowersAdapter(listUser)
        recyclerViewFollowers.adapter = adapter

        adapter.setOnItemClickCallback(object : FollowersAdapter.OnItemClickCallback{
            override fun onItemClicked(position: Int) {
                Log.d(TAG, "TAHU")
                val followerUser: User = listUser.get(position)
                val detailIntent = Intent(requireActivity(), DetailActivity::class.java)
                detailIntent.putExtra(DetailActivity.EXTRA_USER, followerUser)
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(detailIntent)
            }
        })

        favorites = activity!!.intent.getParcelableExtra(DetailActivity.EXTRA_NOTE)
        if (favorites != null){
            dataFavorite = activity!!.intent.getParcelableExtra<Favorite>(EXTRA_NOTE) as Favorite
            getUserFollowers(dataFavorite.username.toString())
        } else {
            val dataUser = activity!!.intent.getParcelableExtra<User>(EXTRA_USER) as User
            getUserFollowers(dataUser.username.toString())
        }
    }

    private fun getUserFollowers(id: String) {
        progressBarFollowers.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d948aa4b3ed82bf58a798691d612a8325bfaab2a")
        val url = "https://api.github.com/users/$id/followers"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBarFollowers?.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val username = jsonObject.getString("login")
                        getUserDetail(username)
                    }
                } catch (e: Exception){
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBarFollowers.visibility = View.INVISIBLE
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode ; Forbidden"
                    404 -> "$statusCode ; Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserDetail(id: String){
        progressBarFollowers.visibility = View.VISIBLE
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
                progressBarFollowers.visibility = View.INVISIBLE
                val result =  String(responseBody)
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
                    listUser.add(
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
                    adapter.notifyDataSetChanged()
                } catch (e: Exception){
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBarFollowers.visibility = View.INVISIBLE
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode ; Forbidden"
                    404 -> "$statusCode ; Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}