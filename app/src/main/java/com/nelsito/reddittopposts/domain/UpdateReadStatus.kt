package com.nelsito.reddittopposts.domain

class UpdateReadStatus(val postStatusService: PostStatusService) {
    operator fun invoke(unreadPost: RedditPost): RedditPost {
        return postStatusService.updateStatus(unreadPost)
    }

}
