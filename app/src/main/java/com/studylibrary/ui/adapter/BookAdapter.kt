package com.studylibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.studylibrary.data.model.Book
import com.studylibrary.databinding.ItemBookBinding

/**
 * RecyclerView Adapter for displaying books in grid/list view
 */
class BookAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onFavoriteClick: (String) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding, onBookClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit,
        private val onFavoriteClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.apply {
                textTitle.text = book.title
                textAuthor.text = book.author
                ratingBar.rating = book.rating
                textProgress.text = "${book.readingProgress.toInt()}% read"
                progressBar.progress = book.readingProgress.toInt()

                imageCover.load(book.coverImageUrl) {
                    crossfade(true)
                }

                btnFavorite.isChecked = book.isFavorite
                btnFavorite.setOnClickListener {
                    onFavoriteClick(book.id)
                }

                root.setOnClickListener {
                    onBookClick(book)
                }
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
