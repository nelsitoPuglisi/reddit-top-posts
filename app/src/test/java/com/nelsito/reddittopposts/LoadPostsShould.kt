package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.*
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.Test

class LoadPostsShould {

    @Test
    fun return_posts_with_read_status() = runBlockingTest {

        val redditPostRepository = aRepositoryWithUnreadPosts()
        val postStatusService = aPostStatusService()

        val loadPosts = LoadPosts(redditPostRepository, postStatusService)

        val posts = loadPosts()

        val expected = aListOfPostsWithFirstOneRead()
        Assertions.assertThat(posts).isEqualTo(expected)
    }

    private fun aPostStatusService(): PostStatusService {
        val postStatus = mapOf("1" to true)
        return PostStatusServiceStub(postStatus)
    }

    private fun aListOfPostsWithFirstOneRead(): TopPostsPage {
        return TopPostsPage(posts = listOf(
            RedditPost("1", "First Post", "John Doe", 1234, 11, "some url", true),
            RedditPost("2", "Second Post", "Jane Doe", 5678, 88, "some url"),
            RedditPost("3", "Third Post", "Bunchy", 4321, 55, "some url")
        ), nextPage = "1")
    }

    private fun aRepositoryWithUnreadPosts(): TopPostsRepository {
        return InMemoryTopPostsRepository(TopPostsPage(posts = listOf(
            RedditPost("1", "First Post", "John Doe", 1234, 11, "some url"),
            RedditPost("2", "Second Post", "Jane Doe", 5678, 88, "some url"),
            RedditPost("3", "Third Post", "Bunchy", 4321, 55, "some url")
        ), nextPage = "1"))
    }
}