package com.example.consumerapp.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.consumerapp.R
import com.example.consumerapp.model.User
import kotlinx.android.synthetic.main.item_user.view.*
import kotlin.collections.ArrayList



class UserAdapter(private val listUser: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ListViewHolder>(){

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            with(itemView){
                Glide.with(itemView.context)
                    .load(user.photo)
                    .apply(RequestOptions().override(250, 250))
                    .into(img_photo)
                txt_username.text = user.username
                txt_name.text = user.name
                txt_location.text = user.location
                txt_company.text = user.company

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(user) }

            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}

