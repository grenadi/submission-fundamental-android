package com.example.githubuser.viewModel

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubuser.CustomOnItemClickListener
import com.example.githubuser.DetailActivity
import com.example.githubuser.R
import com.example.githubuser.model.Favorite
import kotlinx.android.synthetic.main.item_user.view.*

class FavouriteAdapter(private val activity: Activity) : RecyclerView.Adapter<FavouriteAdapter.FavViewHolder>() {
    var listFav = ArrayList<Favorite>()
        set(listFav){
            if (listFav.size > 0){
                this.listFav.clear()
            }
            this.listFav.addAll(listFav)
            notifyDataSetChanged()
        }


    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(favorite: Favorite){
            with(itemView){
                Glide.with(itemView.context)
                        .load(favorite.photo)
                        .apply(RequestOptions().override(250, 250))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(itemView.img_photo)
                txt_username.text = favorite.username
                txt_name.text = favorite.name
                txt_location.text = favorite.location
                txt_company.text = favorite.company
                itemView.setOnClickListener(
                        CustomOnItemClickListener(
                                adapterPosition,
                                object : CustomOnItemClickListener.OnItemClickCallback{
                                    override fun onItemClicked(view: View, position: Int) {
                                        val intent = Intent(activity, DetailActivity::class.java)
                                        intent.putExtra(DetailActivity.EXTRA_POSITION, position)
                                        intent.putExtra(DetailActivity.EXTRA_NOTE, favorite)
                                        activity.startActivity(intent)
                                    }
                                }
                        )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return FavViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.listFav.size
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(listFav[position])
    }

}