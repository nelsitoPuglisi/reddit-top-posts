package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.LoadPosts
import com.nelsito.reddittopposts.domain.PostStatusService
import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.TopPostsRepository
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

    private fun aListOfPostsWithFirstOneRead(): List<RedditPost> {
        return listOf(
            RedditPost("1", "First Post", "John Doe", 1234, 11, "some url", true),
            RedditPost("2", "Second Post", "Jane Doe", 5678, 88, "some url"),
            RedditPost("3", "Third Post", "Bunchy", 4321, 55, "some url")
        )
    }

    private fun aRepositoryWithUnreadPosts(): TopPostsRepository {
        return InMemoryTopPostsRepository(listOf(
            RedditPost("1", "First Post", "John Doe", 1234, 11, "some url"),
            RedditPost("2", "Second Post", "Jane Doe", 5678, 88, "some url"),
            RedditPost("3", "Third Post", "Bunchy", 4321, 55, "some url")
        ))
    }
}