package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.TopPostsRepository

class InMemoryTopPostsRepository(private val aListOfUnreadPosts: List<RedditPost>) : TopPostsRepository {
    override suspend fun firstPage(): List<RedditPost> {
        return aListOfUnreadPosts
    }

}
