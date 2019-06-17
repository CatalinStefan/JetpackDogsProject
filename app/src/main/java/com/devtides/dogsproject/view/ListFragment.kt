package com.devtides.dogsproject.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.devtides.dogsproject.R
import com.devtides.dogsproject.databinding.FragmentListBinding
import com.devtides.dogsproject.viewmodel.ListViewModel

class ListFragment : Fragment() {

    private lateinit var dataBinding: FragmentListBinding
    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dataBinding.dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer {dogs ->
            dogs?.let {
                dataBinding.dogsList.visibility = View.VISIBLE
                dogsListAdapter.updateDogList(it) }
        })

        viewModel.dogsLoadError.observe(this, Observer { isError ->
            isError?.let { dataBinding.listError.visibility = if(it) View.VISIBLE else View.GONE }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
                if(it) {
                    dataBinding.listError.visibility = View.GONE
                    dataBinding.dogsList.visibility = View.GONE
                }
            }
        })
    }override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                Navigation.findNavController(dataBinding.root).navigate(ListFragmentDirections.actionSettings())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
