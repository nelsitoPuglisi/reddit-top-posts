package com.nelsito.reddittopposts.infrastructure

import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.domain.TopPostsPage
import com.nelsito.reddittopposts.domain.TopPostsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class TopPostsNetworkRepository : TopPostsRepository {
    private var client: RedditPostNetworkClient

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okhttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client = retrofit.create(RedditPostNetworkClient::class.java)
    }

    override suspend fun firstPage(): TopPostsPage {
        return toRedditPost(client.top())
    }

    override suspend fun nextPage(previousPage: TopPostsPage): TopPostsPage {
        return toRedditPost(client.top(previousPage.nextPage))
    }

    private fun toRedditPost(redditPostResponse: RedditPostResponse) : TopPostsPage {
        return TopPostsPage(redditPostResponse.data.after, redditPostResponse.data.children.map {
            RedditPost(it.data.id, it.data.title, it.data.author, it.data.created, it.data.num_comments, it.data.thumbnail)
        })
    }
}

interface RedditPostNetworkClient {
    @GET("top")
    suspend fun top(): RedditPostResponse

    @GET("top")
    suspend fun top(@Query("after") after: String): RedditPostResponse

}