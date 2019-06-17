package com.devtides.dogsproject.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.devtides.dogsproject.R
import com.devtides.dogsproject.databinding.FragmentDetailBinding
import com.devtides.dogsproject.viewmodel.DetailViewModel
import com.devtides.dogsproject.viewmodel.ListViewModel

class DetailFragment : Fragment() {

    private lateinit var dataBinding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid ?: 0
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            dataBinding.dog = dog
        })
    }
}
