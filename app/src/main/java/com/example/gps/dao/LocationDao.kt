package com.example.gps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gps.model.LocationData
import com.example.gps.model.MovementData

@Dao
interface LocationDao {
    @Query("SELECT * FROM LocationData")
    fun getAllLocationData(): MutableList<LocationData>
    @Query("SELECT * FROM LocationData where movementDataId = :movementDataId")
    fun getLocationData(movementDataId: Int): MutableList<LocationData>

    @Query("INSERT INTO LocationData (latitude, longitude, movementDataId) VALUES (:latitude, :longitude, :movementDataId)")
    fun insertLocationData(latitude: Double, longitude: Double, movementDataId: Int)

}