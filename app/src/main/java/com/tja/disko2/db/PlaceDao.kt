package com.tja.disko2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tja.disko2.domain.PlaceO2

@Dao
interface PlaceDao {

    @Query("SELECT * FROM place_items ORDER BY name ASC")
    fun getAll(): LiveData<List<PlaceO2>>

    @Query("SELECT * FROM place_items WHERE favorite = 1")
    fun getAllFavorite(): LiveData<List<PlaceO2>>

    @Query("SELECT * FROM place_items WHERE name =:name")
    fun getPlaceByName(name : String) : PlaceO2

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg placeO2: PlaceO2)

    @Update
    suspend fun update(placeO2: PlaceO2)

    @Delete
    suspend fun delete(placeO2: PlaceO2)

}