package com.example.consumerapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favorite(
        var photo: String? = "",
        var name: String? = "",
        var username: String? = "",
        var company: String? = "",
        var location: String? = "",
        var repository: String? = "",
        var followers: String? = "",
        var following: String? = "",
        var isFav: String? = ""
) : Parcelable