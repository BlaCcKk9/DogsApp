package com.example.dogsapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dogsapp.model.DogBreed
import com.example.dogsapp.model.database.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application): BaseViewModel(application) {

    val dog = MutableLiveData<DogBreed>()

    fun refresh(dogId: Int){
        fetchDog(dogId)
    }

    private fun fetchDog(dogId: Int){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dog.value = dao.readDog(dogId)
        }
    }
}