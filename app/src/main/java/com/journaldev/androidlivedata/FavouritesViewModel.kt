package com.journaldev.androidlivedata

import android.app.Application
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.journaldev.androidlivedata.db.DbSettings
import com.journaldev.androidlivedata.db.FavouritesDBHelper
import java.util.*

class FavouritesViewModel internal constructor(application: Application?) : AndroidViewModel(application!!) {
    private val mFavHelper: FavouritesDBHelper = FavouritesDBHelper(application)
    private var mFavs: MutableLiveData<List<Favourites>>? = null
    val favs: MutableLiveData<List<Favourites>>
        get() {
            if (mFavs == null) {
                mFavs = MutableLiveData()
                loadFavs()
            }
            return mFavs!!
        }

    private fun loadFavs() {
        val newFavs: MutableList<Favourites> = ArrayList()
        val db = mFavHelper.readableDatabase
        val cursor = db.query(DbSettings.DBEntry.TABLE, arrayOf(
                DbSettings.DBEntry._ID,
                DbSettings.DBEntry.COL_FAV_URL,
                DbSettings.DBEntry.COL_FAV_DATE
        ),
                null, null, null, null, null)
        while (cursor.moveToNext()) {
            val idxId = cursor.getColumnIndex(DbSettings.DBEntry._ID)
            val idxUrl = cursor.getColumnIndex(DbSettings.DBEntry.COL_FAV_URL)
            val idxDate = cursor.getColumnIndex(DbSettings.DBEntry.COL_FAV_DATE)
            newFavs.add(Favourites(cursor.getLong(idxId), cursor.getString(idxUrl), cursor.getLong(idxDate)))
        }
        cursor.close()
        db.close()
        mFavs!!.value = newFavs
    }

    fun addFav(url: String?, date: Long) {
        val db = mFavHelper.writableDatabase
        val values = ContentValues()
        values.put(DbSettings.DBEntry.COL_FAV_URL, url)
        values.put(DbSettings.DBEntry.COL_FAV_DATE, date)
        val id = db.insertWithOnConflict(DbSettings.DBEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        val favourites = mFavs!!.value
        val clonedFavs: ArrayList<Favourites>
        if (favourites == null) {
            clonedFavs = ArrayList()
        } else {
            clonedFavs = ArrayList(favourites.size)
            for (i in favourites.indices) {
                clonedFavs.add(Favourites(favourites[i]))
            }
        }
        val fav = Favourites(id, url!!, date)
        clonedFavs.add(fav)
        mFavs!!.value = clonedFavs
    }

    fun editEav(id:Long,msg:String?, date: Long){
        val db = mFavHelper.writableDatabase

    }

    fun removeFav(id: Long) {
        val db = mFavHelper.writableDatabase
        db.delete(
                DbSettings.DBEntry.TABLE,
                DbSettings.DBEntry._ID + " = ?", arrayOf(java.lang.Long.toString(id)))
        db.close()
        val favs = mFavs!!.value!!
        val clonedFavs = ArrayList<Favourites>(favs.size)
        for (i in favs.indices) {
            clonedFavs.add(Favourites(favs[i]))
        }
        var index = -1
        for (i in clonedFavs.indices) {
            val favourites = clonedFavs[i]
            if (favourites.mId == id) {
                index = i
            }
        }
        if (index != -1) {
            clonedFavs.removeAt(index)
        }
        mFavs!!.value = clonedFavs
    }

}