package com.nelsito.reddittopposts.domain

class LoadPosts(private val topPostsRepository: TopPostsRepository, private val postStatusService: PostStatusService) {
    suspend operator fun invoke(): TopPostsPage {
        val firstPage = topPostsRepository.firstPage()
        return firstPage.copy(posts = postStatusService.applyStatus(firstPage.posts))
    }
}

interface PostStatusService {
    fun applyStatus(posts: List<RedditPost>): List<RedditPost>
}

interface TopPostsRepository {
    suspend fun firstPage(): TopPostsPage
    suspend fun nextPage(previousPage: TopPostsPage): TopPostsPage
}
