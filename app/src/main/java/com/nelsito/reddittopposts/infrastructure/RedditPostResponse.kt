package com.nelsito.reddittopposts.infrastructure

data class RedditPostResponse(val kind: String, val data: RedditPostResponseData)

data class RedditPostResponseData(val after: String, val children: List<RedditPostResponseDataItem>)

data class RedditPostResponseDataItem(val kind: String, val data: RedditPostResponseDataItemData)

//REAL DATA
data class RedditPostResponseDataItemData(val id: String, val title: String, val thumbnail: String, val author: String, val num_comments: Int, val created: Long)