package com.nelsito.reddittopposts

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.reddittopposts.domain.*
import com.nelsito.reddittopposts.infrastructure.InMemoryPostStatusService
import com.nelsito.reddittopposts.infrastructure.TopPostsNetworkRepository
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.android.synthetic.main.item_list_content.view.post_author
import kotlinx.android.synthetic.main.item_list_content.view.post_comments_count
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : ItemListListener, AppCompatActivity() {
    companion object {
        const val FRAGMENT_TAG = "DETAIL_FRAGMENT"
    }
    private lateinit var lastPage: TopPostsPage
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    lateinit var myRecycler:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title
        swipeContainer.setOnRefreshListener {
            setupRecyclerView(item_list)
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        myRecycler = item_list
        myRecycler.itemAnimator = DefaultItemAnimator()
        val linearLayoutManager = LinearLayoutManager(this)
        myRecycler.layoutManager = linearLayoutManager

        myRecycler.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadNextPage()
            }
        })

        if (savedInstanceState?.containsKey("LOADED_POSTS") == true) {
            val posts: Array<RedditPost> = savedInstanceState.getParcelableArray("LOADED_POSTS") as Array<RedditPost>
            showRecyclerView(myRecycler, posts.toMutableList())
        } else {
            setupRecyclerView(myRecycler)
        }

        btn_dismiss_all.setOnClickListener {
            removeDetailFragment()
            val adapter = myRecycler.adapter as SimpleItemRecyclerViewAdapter
            adapter.dismissAll()
            btn_dismiss_all.visibility = View.GONE
        }
    }

    private fun loadNextPage() {
        GlobalScope.launch(Dispatchers.Main) {
            lastPage = loadNextPagePosts(lastPage)
            val adapter = myRecycler.adapter as SimpleItemRecyclerViewAdapter
            adapter.addMore(lastPage.posts.toMutableList())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val adapter = myRecycler.adapter as SimpleItemRecyclerViewAdapter
        outState.putParcelableArray("LOADED_POSTS", adapter.values.toTypedArray())
    }

    //TODO: Move instantiation to DI provider
    val topPostsRepository = TopPostsNetworkRepository()
    val postStatusService = InMemoryPostStatusService()
    val loadPosts = LoadPosts(topPostsRepository, postStatusService)
    val loadNextPagePosts = LoadPostsNextPage(topPostsRepository, postStatusService)
    val updateReadStatus = UpdateReadStatus(postStatusService)

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        GlobalScope.launch(Dispatchers.Main) {
            lastPage = loadPosts()
            showRecyclerView(recyclerView, lastPage.posts)
        }
    }

    private fun showRecyclerView(
        recyclerView: RecyclerView,
        posts: List<RedditPost>
    ) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            this@ItemListActivity,
            posts.toMutableList(),
            this
        )
        progress.visibility = View.GONE
        swipeContainer.isRefreshing = false
        btn_dismiss_all.visibility = View.VISIBLE
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        var values: MutableList<RedditPost>,
        private val itemListListener: ItemListListener
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        fun dismissItem(position: Int) {
            values.removeAt(position)
            notifyItemRemoved(position)
        }

        fun dismissAll() {
            for (i in values.indices) {
                values.removeAt(0)
                notifyItemRemoved(0)
            }
        }

        fun addMore(morePosts: List<RedditPost>) {
            val size = values.size
            values.addAll(morePosts)
            notifyItemRangeInserted(size, morePosts.size)
        }

        fun changePost(post : RedditPost) {
            val index = values.indexOfFirst { it.id == post.id }
            values[index] = post
            notifyItemChanged(index)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.title.text = item.title

            if (item.read) {
                holder.title.setTypeface(null, Typeface.NORMAL)
                holder.rootView.setBackgroundColor(Color.parseColor("#CACACA"))
            } else {
                holder.title.setTypeface(null, Typeface.BOLD)
                holder.rootView.setBackgroundColor(Color.parseColor("#ADADAD"))
            }

            holder.commentsCount.text = parentActivity.getString(R.string.comments_count, item.comments)
            holder.author.text = parentActivity.getString(R.string.author, item.author, prettyTime(item.timestamp))

            if (!item.thumbnail.startsWith("http")) holder.picture.visibility = View.GONE else {
                holder.picture.visibility = View.VISIBLE
                Glide
                    .with(parentActivity)
                    .load(item.thumbnail)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(holder.picture)
            }

            holder.dismiss.setOnClickListener {
                itemListListener.onItemDismiss(holder.adapterPosition)
            }

            holder.itemView.setOnClickListener {
                itemListListener.openDetail(item)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val dismiss: ImageView = view.btn_dismiss
            val title: TextView = view.post_title
            val author: TextView = view.post_author
            val commentsCount: TextView = view.post_comments_count
            val picture: ImageView = view.post_thumbnail
            val rootView = view
        }
    }

    override fun onItemDismiss(position: Int) {
        (myRecycler.adapter as SimpleItemRecyclerViewAdapter).dismissItem(position)
        removeDetailFragment()
    }

    override fun openDetail(redditPost: RedditPost) {
        val readPost = updateReadStatus(redditPost)
        (myRecycler.adapter as SimpleItemRecyclerViewAdapter).changePost(readPost)

        if (twoPane) {
            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ItemDetailFragment.ARG_POST_DETAIL, readPost)
                }
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.item_detail_container, fragment, FRAGMENT_TAG)
                .commit()
        } else {
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra(ItemDetailFragment.ARG_POST_DETAIL, readPost)
            }
            startActivity(intent)
        }
    }

    private fun removeDetailFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .remove(fragment)
                .commit()
        }
    }
}

interface ItemListListener {
    fun onItemDismiss(position: Int)
    fun openDetail(redditPost: RedditPost)
}

private fun prettyTime(timestamp: Long): String {
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(Date(timestamp*1000))
}
