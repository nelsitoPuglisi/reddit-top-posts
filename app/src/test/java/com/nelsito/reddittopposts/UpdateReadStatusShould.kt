package com.nelsito.reddittopposts

import com.nelsito.reddittopposts.domain.PostStatusService
import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.UpdateReadStatus
import org.assertj.core.api.Assertions
import org.junit.Test

class UpdateReadStatusShould {
    @Test
    fun save_post_status() {
        val updateReadStatus = UpdateReadStatus(aPostStatusService())

        val unreadPost = anUnreadPost()

        val updatedPost = updateReadStatus(unreadPost)

        Assertions.assertThat(updatedPost).isEqualTo(aReadPost())
    }

    private fun aPostStatusService(): PostStatusService {
        val postStatus = setOf<String>()
        return PostStatusServiceStub(postStatus)
    }

    private fun aReadPost(): RedditPost {
        return RedditPost("1", "title", "John Doe", 1234, 888, "some url", true)
    }

    private fun anUnreadPost(): RedditPost {
        return RedditPost("1", "title", "John Doe", 1234, 888, "some url")
    }
}