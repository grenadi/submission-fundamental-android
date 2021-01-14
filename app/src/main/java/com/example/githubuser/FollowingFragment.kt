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
import com.example.githubuser.viewModel.FollowingAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_following.*
import org.json.JSONArray
import org.json.JSONObject


class FollowingFragment : Fragment() {

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_NOTE = "extra_note"
    }

    private val listUser: ArrayList<User> = ArrayList()
    private lateinit var adapter: FollowingAdapter
    private var favorites: Favorite? = null
    private lateinit var dataFavorite: Favorite
    private lateinit var dataUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listUser.clear()

        recyclerViewFollowing.layoutManager = LinearLayoutManager(activity)
        adapter = FollowingAdapter(listUser)
        recyclerViewFollowing.adapter = adapter

        adapter.setOnItemClickCallback(object : FollowingAdapter.OnItemClickCallback{
            override fun onItemClicked(position: Int) {
                val followingUser: User = listUser.get(position)
                val detailIntent = Intent(requireActivity(), DetailActivity::class.java)
                detailIntent.putExtra(DetailActivity.EXTRA_USER, followingUser)
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(detailIntent)
            }
        })

        favorites = activity!!.intent.getParcelableExtra(DetailActivity.EXTRA_NOTE)
        if (favorites != null){
            dataFavorite = activity!!.intent.getParcelableExtra<Favorite>(EXTRA_NOTE) as Favorite
            getUserFollowing(dataFavorite.username.toString())
        } else {
            val dataUser = activity!!.intent.getParcelableExtra<User>(EXTRA_USER) as User
            getUserFollowing(dataUser.username.toString())
        }
    }

    private fun getUserFollowing(id: String) {
        progressBarFollowing.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d948aa4b3ed82bf58a798691d612a8325bfaab2a")
        val url = "https://api.github.com/users/$id/following"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                progressBarFollowing?.visibility = View.INVISIBLE
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
                progressBarFollowing.visibility = View.INVISIBLE
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserDetail(id: String) {
        progressBarFollowing.visibility = View.VISIBLE
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
                progressBarFollowing.visibility = View.INVISIBLE
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
                progressBarFollowing.visibility = View.INVISIBLE
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}