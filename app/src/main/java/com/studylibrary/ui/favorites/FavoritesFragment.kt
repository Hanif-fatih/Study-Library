package com.studylibrary.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.studylibrary.databinding.FragmentFavoritesBinding
import com.studylibrary.ui.adapter.BookAdapter
import com.studylibrary.ui.viewmodel.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying user's favorite books
 */
@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookListViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        collectStates()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { /* Navigate to detail */ },
            onFavoriteClick = { bookId -> viewModel.toggleFavorite(bookId) }
        )
        binding.recyclerFavorites.adapter = bookAdapter
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.booksState.collect { state ->
                // Update UI based on state
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
