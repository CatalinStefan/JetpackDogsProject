package com.devtides.dogsproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.devtides.dogsproject.model.DogBreed
import com.devtides.dogsproject.model.DogDatabase
import com.devtides.dogsproject.model.DogsApiService
import com.devtides.dogsproject.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application): BaseViewModel(application) {

    private val dogsService = DogsApiService()
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private val refreshTime = 5L * 60 * 1000 * 1000 * 1000

    fun refresh() {
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    private fun fetchFromDatabase() {
        loading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        dogsRetrieved(dogList)
                        Toast.makeText(getApplication(), "Dogs retrieved from remote endpoint", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                    }

                })
        )
    }

    private fun dogsRetrieved(dogList: List<DogBreed>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreed>) {
        launch {
            DogDatabase(getApplication()).dogDao().deleteAllDogs()
            DogDatabase(getApplication()).dogDao().insertAll(*list.toTypedArray())
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}