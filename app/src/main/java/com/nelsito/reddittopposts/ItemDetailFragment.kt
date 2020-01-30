package com.nelsito.reddittopposts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.nelsito.reddittopposts.domain.RedditPost
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private lateinit var redditPost: RedditPost

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_POST_DETAIL = "reddit_post" +
                ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_POST_DETAIL)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                redditPost = it.getParcelable(ARG_POST_DETAIL)!!

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        redditPost.let {
            rootView.title.text = it.title
            rootView.post_comments_count.text = getString(R.string.comments_count, it.comments)
            rootView.post_author.text = getString(R.string.author, it.author, prettyTime(it.timestamp))

            if (!it.thumbnail.startsWith("http")) rootView.picture.visibility = View.GONE else {
                Glide
                    .with(this)
                    .load(redditPost.thumbnail)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(rootView.picture)
            }
        }

        return rootView
    }
}

private fun prettyTime(timestamp: Long): String {
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(Date(timestamp*1000))
}
