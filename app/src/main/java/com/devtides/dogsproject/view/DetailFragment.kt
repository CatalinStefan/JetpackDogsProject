package com.devtides.dogsproject.view


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.devtides.dogsproject.R

import com.devtides.dogsproject.databinding.FragmentDetailBinding
import com.devtides.dogsproject.model.DogBreed
import com.devtides.dogsproject.model.DogPalette
import com.devtides.dogsproject.viewmodel.DetailViewModel


class DetailFragment : Fragment() {

    private lateinit var dataBinding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0
    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        setHasOptionsMenu(true)
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

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            currentDog = dog
            dataBinding.dog = dog

            dog?.imageUrl?.let {
                setupBackgroundColor(it)
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        val intColor = palette?.vibrantSwatch?.rgb ?: 0
                        val myPalette = DogPalette(intColor)
                        dataBinding.palette = myPalette
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.detail_menu, menu)
//    }
}
