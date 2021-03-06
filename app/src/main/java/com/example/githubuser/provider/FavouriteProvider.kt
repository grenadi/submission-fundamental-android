package com.example.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.githubuser.db.DatabaseContract.AUTHORITY
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.example.githubuser.db.FavoriteHelper

class FavouriteProvider : ContentProvider(){

    companion object{
        private const val FAV = 1
        private const val FAV_ID = 2
        private lateinit var favHelper: FavoriteHelper
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV)
        sUriMatcher.addURI(
                AUTHORITY,
                "$TABLE_NAME/#",
                FAV_ID
        )
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (FAV){
            sUriMatcher.match(uri) -> favHelper.insert(contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun query(
            uri: Uri,
            strings: Array<String>?,
            s: String?,
            strings1: Array<String>?,
            s1: String?
    ): Cursor? {
        return when (sUriMatcher.match(uri)){
            FAV -> favHelper.queryAll()
            FAV_ID -> favHelper.queryById(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun onCreate(): Boolean {
        favHelper = FavoriteHelper.getInstance(context as Context)
        favHelper.open()
        return true
    }

    override fun update(
            uri: Uri,
            contentValues: ContentValues?,
            s: String?,
            strings: Array<String>?
    ): Int {
        val updated: Int = when (FAV_ID){
            sUriMatcher.match(uri) -> favHelper.update(
                    uri.lastPathSegment.toString(),
                    contentValues
            )
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val deleted: Int = when (FAV_ID){
            sUriMatcher.match(uri) -> favHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

}