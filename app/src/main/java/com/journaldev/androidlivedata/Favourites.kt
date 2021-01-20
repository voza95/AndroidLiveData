package com.journaldev.androidlivedata

class Favourites {
    @JvmField
    var mId: Long
    @JvmField
    var mUrl: String
    @JvmField
    var mDate: Long

    constructor(id: Long, name: String, date: Long) {
        mId = id
        mUrl = name
        mDate = date
    }

    constructor(favourites: Favourites) {
        mId = favourites.mId
        mUrl = favourites.mUrl
        mDate = favourites.mDate
    }
}