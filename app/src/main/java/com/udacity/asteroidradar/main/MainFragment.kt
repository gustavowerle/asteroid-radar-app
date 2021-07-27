package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: AsteroidsAdapter

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModelProvider(requireContext())
        ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = AsteroidsAdapter(AsteroidsAdapter.OnClickListener {
            viewModel.displayAsteroidDetail(it)
            viewModel.displayAsteroidDetailComplete()
        })
        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)

        setPictureOfDayObserver()
        setAsteroidsListObserver()
        setNavigateToAsteroidDetailObserver()

        return binding.root
    }

    private fun setNavigateToAsteroidDetailObserver() {
        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer {
            if (it != null)
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })
    }

    private fun setAsteroidsListObserver() {
        viewModel.asteroidsList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun setPictureOfDayObserver() {
        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            if (Constants.MEDIA_TYPE_IMAGE == it.mediaType) {
                binding.fragmentDetailAsteroidImage.contentDescription = it.title
                context?.apply {
                    Glide.with(this)
                        .load(it.url)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(binding.fragmentDetailAsteroidImage)

                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_asteroids_menu -> {
                viewModel.filterAsteroids(MainViewModel.WEEK_ASTEROIDS)
            }
            R.id.show_today_asteroids_menu -> {
                viewModel.filterAsteroids(MainViewModel.TODAY_ASTEROIDS)
            }
            R.id.show_saved_asteroids_menu -> {
                viewModel.filterAsteroids(MainViewModel.SAVED_ASTEROIDS)
            }
        }
        return true
    }
}
