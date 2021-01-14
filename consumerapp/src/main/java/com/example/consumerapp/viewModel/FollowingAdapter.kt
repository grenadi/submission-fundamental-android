package com.example.consumerapp.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.consumerapp.R
import com.example.consumerapp.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_user.view.*

var followingFilterList = ArrayList<User>()

class FollowingAdapter(listUser: ArrayList<User>) : RecyclerView.Adapter<FollowingAdapter.ListViewHolder>() {

    init {
        followingFilterList = listUser
    }

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAvatar: CircleImageView = itemView.img_photo
        var txtUsername: TextView = itemView.txt_username
        var txtName: TextView = itemView.txt_name
        var txtCompany: TextView = itemView.txt_company
        var txtLocation: TextView = itemView.txt_location

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user, viewGroup, false)
        val sch = ListViewHolder(view)
        mcontext = viewGroup.context
        return sch
    }

    override fun getItemCount(): Int = followingFilterList.size


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = followingFilterList[position]
        Glide.with(holder.itemView.context)
            .load(data.photo)
            .apply(RequestOptions().override(250, 250))
            .into(holder.imgAvatar)
        holder.txtUsername.text = data.username
        holder.txtName.text = data.name
        holder.txtCompany.text = data.company
        holder.txtLocation.text = data.location
        holder.itemView.setOnClickListener {
            this.onItemClickCallback?.onItemClicked(position)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(position: Int)
    }
}