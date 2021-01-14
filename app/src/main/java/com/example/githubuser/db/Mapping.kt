package com.example.githubuser.db

import android.database.Cursor
import com.example.githubuser.model.Favorite

object Mapping {
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Favorite>{
        val favList = ArrayList<Favorite>()

        notesCursor?.apply {
            while (moveToNext()){
                val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
                val name = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.NAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COMPANY))
                val location = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.LOCATION))
                val repository = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.REPOSITORY))
                val followers = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.FOLLOWERS))
                val following = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.FOLLOWING))
                val favourite = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.FAVOURITE))
                favList.add(
                        Favorite(
                                avatar,
                                name,
                                username,
                                company,
                                location,
                                repository,
                                followers,
                                following,
                                favourite
                        )
                )
            }
        }
        return favList
    }
}