package com.nelsito.reddittopposts.domain

class LoadPosts(private val topPostsRepository: TopPostsRepository, private val postStatusService: PostStatusService) {
    suspend operator fun invoke(): TopPostsPage {
        val firstPage = topPostsRepository.firstPage()
        return firstPage.copy(posts = postStatusService.applyStatus(firstPage.posts))
    }
}

class LoadPostsNextPage(private val topPostsRepository: TopPostsRepository, private val postStatusService: PostStatusService) {
    suspend operator fun invoke(previousPage: TopPostsPage): TopPostsPage {
        val nextPage = topPostsRepository.nextPage(previousPage)
        return nextPage.copy(posts = postStatusService.applyStatus(nextPage.posts))
    }
}

interface PostStatusService {
    fun applyStatus(posts: List<RedditPost>): List<RedditPost>
}

interface TopPostsRepository {
    suspend fun firstPage(): TopPostsPage
    suspend fun nextPage(previousPage: TopPostsPage): TopPostsPage
}
