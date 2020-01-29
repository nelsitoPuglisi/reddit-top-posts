package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.PostStatusService
import com.nelsito.reddittopposts.domain.RedditPost

class PostStatusServiceStub(postStatus: Map<String, Boolean>) : PostStatusService {
    override fun applyStatus(posts: List<RedditPost>): List<RedditPost> {
        return posts
    }
}
