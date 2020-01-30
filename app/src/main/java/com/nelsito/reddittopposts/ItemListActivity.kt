package com.nelsito.reddittopposts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.reddittopposts.domain.LoadPosts
import com.nelsito.reddittopposts.domain.RedditPost
import com.nelsito.reddittopposts.infrastructure.InMemoryPostStatusService
import com.nelsito.reddittopposts.infrastructure.TopPostsNetworkRepository
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
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
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    val defaultItemAnimator = DefaultItemAnimator()
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
        myRecycler.itemAnimator = defaultItemAnimator
        setupRecyclerView(myRecycler)

        btn_dismiss_all.setOnClickListener {
            val adapter = myRecycler.adapter as SimpleItemRecyclerViewAdapter
            adapter.dismissAll()
            btn_dismiss_all.visibility = View.GONE
        }
    }

    //TODO: Move instantiation to DI provider
    val loadPosts = LoadPosts(TopPostsNetworkRepository(), InMemoryPostStatusService())

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        GlobalScope.launch(Dispatchers.Main) {
            val topPosts = loadPosts()
            recyclerView.adapter = SimpleItemRecyclerViewAdapter(this@ItemListActivity, topPosts.posts.toMutableList(), twoPane)
            progress.visibility = View.GONE
            swipeContainer.isRefreshing = false
            btn_dismiss_all.visibility = View.VISIBLE
        }
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private var values: MutableList<RedditPost>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener
        private val onDismissListener: DismissListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as RedditPost
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }

            onDismissListener = object : DismissListener {
                override fun onDismiss(view: View) {
                    val position = parentActivity.myRecycler.getChildAdapterPosition(view)
                    values.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }

        fun dismissAll() {
            for (i in values.indices) {
                values.removeAt(0)
                notifyItemRemoved(0)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.title.text = item.title
            holder.commentsCount.text = parentActivity.getString(R.string.comments_count, item.comments)
            holder.author.text = parentActivity.getString(R.string.author, item.author, prettyTime(item.timestamp))

            Glide
                .with(parentActivity)
                .load(item.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(holder.picture)

            holder.dismiss.setOnClickListener {
                onDismissListener.onDismiss(holder.rootView)
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
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
}

interface DismissListener {
    fun onDismiss(view: View)
}

private fun prettyTime(timestamp: Long): String {
    val prettyTime = PrettyTime(Locale.getDefault())
    return prettyTime.format(Date(timestamp*1000))
}
