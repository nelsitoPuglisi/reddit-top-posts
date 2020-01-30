package com.nelsito.reddittopposts.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedditPost(val id:String, val title:String, val author:String, val timestamp:Long, val comments:Int, val thumbnail:String, val read: Boolean = false) :
    Parcelable

@Parcelize
data class TopPostsPage(val nextPage: String, val posts: List<RedditPost>) : Parcelable