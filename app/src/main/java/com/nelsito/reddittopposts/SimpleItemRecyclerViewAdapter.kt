package com.nelsito.reddittopposts

import android.graphics.Color
import android.graphics.Typeface.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.reddittopposts.domain.RedditPost
import kotlinx.android.synthetic.main.item_list_content.view.*

class SimpleItemRecyclerViewAdapter(
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

    fun changePost(post: RedditPost) {
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
            holder.title.setTypeface(null, NORMAL)
            holder.rootView.setBackgroundColor(Color.parseColor("#CACACA"))
        } else {
            holder.title.setTypeface(null, BOLD)
            holder.rootView.setBackgroundColor(Color.parseColor("#ADADAD"))
        }

        holder.commentsCount.text =
            holder.itemView.context.getString(R.string.comments_count, item.comments)
        holder.author.text = holder.itemView.context.getString(
            R.string.author,
            item.author,
            prettyTime(item.timestamp)
        )

        if (!item.thumbnail.startsWith("http")) holder.picture.visibility = View.GONE else {
            holder.picture.visibility = View.VISIBLE
            Glide
                .with(holder.itemView.context)
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


interface ItemListListener {
    fun onItemDismiss(position: Int)
    fun openDetail(redditPost: RedditPost)
}
