package com.example.dogsapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsapp.model.DogBreed
import com.example.dogsapp.model.database.DogDatabase
import com.example.dogsapp.model.service.DogsApiService
import com.example.dogsapp.util.NotificationsHelper
import com.example.dogsapp.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class DogsViewModel(application: Application): BaseViewModel(application) {

    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L
    private val sharedPreference = SharedPreferencesHelper(getApplication())
    private val dogService = DogsApiService()
    private val disposable = CompositeDisposable()


    var dogs = MutableLiveData<List<DogBreed>>()
    var loadError = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    fun refresh(){
        checkDuration()
        val updateTime = sharedPreference.getUpdateTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime)
            fetchDogFromDatabase()
        else
            fetchDogFromRemote()
    }

    fun refreshByPassCache(){
        fetchDogFromRemote()
    }

    private fun checkDuration(){
        try {
            val cachePreference = sharedPreference.getCacheDuration()?.toInt() ?: 5 * 60
            refreshTime = cachePreference.times(60 * 1000 * 1000 * 1000L)
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }
    }

    private fun fetchDogFromDatabase(){
        loading.value = true
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            val dogList = dao.readAllDog()
            dogRetrieved(dogList)
            Log.d("DATABASE->>>>", "Retrieved from database")
        }

    }

    private fun fetchDogFromRemote(){
        loading.value = true
        disposable.add(
            dogService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogsList: List<DogBreed>) {
                        storeDogsLocally(dogsList)
                        Log.d("REMOTE->>>", "Retrieved from remote")
                        var i = 0
                        val dog = dogsList.single { it.dogBreed == "Akita" }
                        NotificationsHelper(getApplication()).createNotification(dog)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun dogRetrieved(dogsList: List<DogBreed>){
        dogs.value = dogsList
        loadError.value = false
        loading.value = false
    }

    fun storeDogsLocally(dogsList: List<DogBreed>){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDog()

            val result = dao.insertAll(*dogsList.toTypedArray())
            var i = 0
            while (i < dogsList.size){
                dogsList[i].uuid = result[i].toInt()
                ++i
            }
            dogRetrieved(dogsList)
        }

        sharedPreference.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}