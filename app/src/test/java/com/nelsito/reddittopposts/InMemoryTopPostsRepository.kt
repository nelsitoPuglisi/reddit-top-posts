package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.TopPostsPage
import com.nelsito.reddittopposts.domain.TopPostsRepository

class InMemoryTopPostsRepository(private val aListOfUnreadPosts: TopPostsPage) : TopPostsRepository {
    override suspend fun firstPage(): TopPostsPage {
        return aListOfUnreadPosts
    }

    override suspend fun nextPage(previousPage: TopPostsPage): TopPostsPage {
        return aListOfUnreadPosts
    }

}
