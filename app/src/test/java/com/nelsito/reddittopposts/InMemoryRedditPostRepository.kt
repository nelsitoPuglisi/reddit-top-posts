package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.RedditPostRepository

class InMemoryRedditPostRepository(private val aListOfUnreadPosts: List<RedditPost>) : RedditPostRepository {
    override suspend fun topPosts(): List<RedditPost> {
        return aListOfUnreadPosts
    }

}
