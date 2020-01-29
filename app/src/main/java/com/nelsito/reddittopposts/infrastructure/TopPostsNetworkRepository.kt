package com.nelsito.reddittopposts.infrastructure

import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.TopPostsRepository
import retrofit2.Retrofit
import retrofit2.http.GET


class TopPostsNetworkRepository : TopPostsRepository {
    private var client: RedditPostNetworkClient

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .build()

        client = retrofit.create(RedditPostNetworkClient::class.java)
    }

    override suspend fun firstPage(): List<RedditPost> {
        return toRedditPost(client.top())
    }

    private fun toRedditPost(redditPostResponse: RedditPostResponse) : List<RedditPost> {
        return redditPostResponse.data.children.map {
            RedditPost(it.data.id, it.data.title, it.data.author, it.data.created, it.data.num_comments, it.data.thumbnail)
        }
    }
}

interface RedditPostNetworkClient {
    @GET("top")
    suspend fun top(): RedditPostResponse
}