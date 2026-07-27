package com.studylibrary.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.studylibrary.databinding.FragmentBookDetailBinding
import com.studylibrary.ui.viewmodel.BookDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Fragment for displaying book details
 */
@AndroidEntryPoint
class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookDetailViewModel by viewModels()
    private val args: BookDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        collectStates()
    }

    private fun setupListeners() {
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnRead.setOnClickListener {
            val action = BookDetailFragmentDirections.actionDetailToReader(args.bookId)
            findNavController().navigate(action)
        }

        binding.btnDownload.setOnClickListener {
            viewModel.startDownload()
        }
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookState.collect { state ->
                when (state) {
                    is BookDetailViewModel.BookState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is BookDetailViewModel.BookState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val book = state.book
                        binding.apply {
                            textTitle.text = book.title
                            textAuthor.text = book.author
                            textDescription.text = book.description
                            textCategory.text = book.category
                            ratingBar.rating = book.rating
                            imageCover.load(book.coverImageUrl)
                        }
                    }
                    is BookDetailViewModel.BookState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Timber.e(state.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.btnFavorite.isChecked = isFavorite
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
