package com.nelsito.reddittopposts.domain

class LoadPosts(private val topPostsRepository: TopPostsRepository, private val postStatusService: PostStatusService) {
    suspend operator fun invoke(): List<RedditPost> {
        val posts = topPostsRepository.firstPage()
        return postStatusService.applyStatus(posts)
    }
}

interface PostStatusService {
    fun applyStatus(posts: List<RedditPost>): List<RedditPost>
}

interface TopPostsRepository {
    suspend fun firstPage(): List<RedditPost>
}
