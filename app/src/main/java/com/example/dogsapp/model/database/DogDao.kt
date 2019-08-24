package com.example.dogsapp.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dogsapp.model.DogBreed

@Dao
interface DogDao {
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query("SELECT * FROM dogbreed")
    suspend fun readAllDog() : List<DogBreed>

    @Query("SELECT * FROM dogbreed WHERE uuid = :dogId")
    suspend fun readDog(dogId: Int) : DogBreed

    @Query("DELETE FROM dogbreed")
    suspend fun deleteAllDog()

}