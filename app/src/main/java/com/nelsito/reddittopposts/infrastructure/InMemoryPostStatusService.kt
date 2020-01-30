package com.nelsito.reddittopposts.infrastructure

import com.nelsito.reddittopposts.domain.PostStatusService
import com.nelsito.reddittopposts.domain.RedditPost

class InMemoryPostStatusService : PostStatusService {
    private val readPosts = mutableSetOf<String>()
    override fun applyStatus(posts: List<RedditPost>): List<RedditPost> {
        return posts.map { it.copy(read = readPosts.contains(it.id)) }
    }

    override fun updateStatus(unreadPost: RedditPost): RedditPost {
        readPosts.add(unreadPost.id)
        return unreadPost.copy(read = true)
    }
}