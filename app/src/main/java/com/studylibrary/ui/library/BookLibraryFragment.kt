package com.studylibrary.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.studylibrary.databinding.FragmentBookLibraryBinding
import com.studylibrary.ui.adapter.BookAdapter
import com.studylibrary.ui.viewmodel.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Fragment for displaying the book library with grid/list view
 */
@AndroidEntryPoint
class BookLibraryFragment : Fragment() {

    private var _binding: FragmentBookLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookListViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        collectStates()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                val action = BookLibraryFragmentDirections.actionLibraryToDetail(book.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { bookId ->
                viewModel.toggleFavorite(bookId)
            }
        )

        binding.recyclerBooks.apply {
            adapter = bookAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadBooks()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.btnToggleView.setOnClickListener {
            viewModel.toggleViewMode()
        }
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.booksState.collect { state ->
                when (state) {
                    is BookListViewModel.BooksState.Loading -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is BookListViewModel.BooksState.Success -> {
                        binding.swipeRefresh.isRefreshing = false
                        bookAdapter.submitList(state.books)
                    }
                    is BookListViewModel.BooksState.Error -> {
                        binding.swipeRefresh.isRefreshing = false
                        Timber.e(state.message)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
