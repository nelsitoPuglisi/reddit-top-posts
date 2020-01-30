package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.PostStatusService
import com.nelsito.reddittopposts.domain.RedditPost

class PostStatusServiceStub(private val postStatus:Set<String>) : PostStatusService {
    override fun applyStatus(posts: List<RedditPost>): List<RedditPost> {
        return posts.map { it.copy(read = postStatus.contains(it.id)) }
    }

    override fun updateStatus(unreadPost: RedditPost) : RedditPost{
        return unreadPost.copy(read = true)
    }
}
