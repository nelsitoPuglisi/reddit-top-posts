package com.nelsito.reddittopposts.domain

data class RedditPost(val id:String, val title:String, val author:String, val timestamp:Long, val comments:Int, val thumbnail:String, val read: Boolean = false)

data class TopPostsPage(val nextPage: String, val posts: List<RedditPost>)