package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.AsteroidsListAdapter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false)

        // get reference to application context
        val application = requireNotNull(this.activity).application

        // get instance of ViewModelFactory
        val viewModelFactory = MainViewModelFactory(application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidsListAdapter(AsteroidsListAdapter.OnClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    adapter.submitList(it)
                }
        })

        viewModel.nasaImage.observe(viewLifecycleOwner, Observer {
            it?.apply {
                binding.activityMainImageOfTheDay.contentDescription = it.mediaType
                Picasso.with(context)
                    .load(it.url).into(binding.activityMainImageOfTheDay)
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_today_menu -> {
                viewModel.showMenuOptionSelected(MenuOptionSelected.TODAY)
                }
            R.id.show_week_menu -> {
                viewModel.showMenuOptionSelected(MenuOptionSelected.WEEK)
            }
            R.id.show_saved_menu -> {
                viewModel.showMenuOptionSelected(MenuOptionSelected.SAVED)
            }
        }
        return true
    }
}
