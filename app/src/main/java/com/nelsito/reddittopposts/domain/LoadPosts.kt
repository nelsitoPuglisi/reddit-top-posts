package com.nelsito.reddittopposts.domain

class LoadPosts(private val redditPostRepository: RedditPostRepository, private val postStatusService: PostStatusService) {
    suspend operator fun invoke(): List<RedditPost> {
        val posts = redditPostRepository.topPosts()
        return postStatusService.applyStatus(posts)
    }
}

interface PostStatusService {
    fun applyStatus(posts: List<RedditPost>): List<RedditPost>
}

interface RedditPostRepository {
    suspend fun topPosts(): List<RedditPost>
}
