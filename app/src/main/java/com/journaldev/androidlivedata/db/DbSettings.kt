package com.journaldev.androidlivedata.db

import android.provider.BaseColumns

object DbSettings {
    const val DB_NAME = "favourites.db"
    const val DB_VERSION = 1

    object DBEntry : BaseColumns {
        val _ID = "_id"
        const val TABLE = "fav"
        const val COL_FAV_URL = "url"
        const val COL_FAV_DATE = "date"
    }
}
