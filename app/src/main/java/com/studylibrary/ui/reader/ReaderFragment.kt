package com.studylibrary.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.studylibrary.databinding.FragmentReaderBinding
import com.studylibrary.ui.viewmodel.ReaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for advanced book reader with PDF/EPUB support
 */
@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReaderViewModel by viewModels()
    private val args: ReaderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        collectStates()
    }

    private fun setupToolbar() {
        binding.toolbarReader.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnDayMode.setOnClickListener {
            viewModel.setReaderMode(ReaderViewModel.ReaderMode.DAY)
        }
        binding.btnNightMode.setOnClickListener {
            viewModel.setReaderMode(ReaderViewModel.ReaderMode.NIGHT)
        }
        binding.btnSepiaMode.setOnClickListener {
            viewModel.setReaderMode(ReaderViewModel.ReaderMode.SEPIA)
        }

        binding.btnAddBookmark.setOnClickListener {
            viewModel.addBookmark()
        }

        binding.btnAddHighlight.setOnClickListener {
            viewModel.addHighlight("selected text", "#FFFF00")
        }

        binding.btnAddNote.setOnClickListener {
            viewModel.addNote("selected text", "note content")
        }

        binding.btnFullscreen.setOnClickListener {
            viewModel.toggleFullscreen()
        }
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentPage.collect { page ->
                binding.textPageNumber.text = "Page $page"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fontSize.collect { size ->
                binding.readerText.textSize = size.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readerMode.collect { mode ->
                updateReaderTheme(mode)
            }
        }
    }

    private fun updateReaderTheme(mode: ReaderViewModel.ReaderMode) {
        when (mode) {
            ReaderViewModel.ReaderMode.DAY -> {
                binding.readerContainer.setBackgroundColor(0xFFFFFFFF.toInt())
                binding.readerText.setTextColor(0xFF000000.toInt())
            }
            ReaderViewModel.ReaderMode.NIGHT -> {
                binding.readerContainer.setBackgroundColor(0xFF1C1B1F.toInt())
                binding.readerText.setTextColor(0xFFFFFFFF.toInt())
            }
            ReaderViewModel.ReaderMode.SEPIA -> {
                binding.readerContainer.setBackgroundColor(0xFFFFF8DC.toInt())
                binding.readerText.setTextColor(0xFF8B4513.toInt())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
