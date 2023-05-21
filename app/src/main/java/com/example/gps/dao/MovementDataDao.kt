package com.example.gps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gps.model.LocationData
import com.example.gps.model.MovementData

@Dao
interface MovementDataDao {
    @Query("SELECT * FROM MovementData")
    fun getAllMovementData(): MutableList<MovementData>

    @Query("SELECT id FROM MovementData ORDER BY id DESC LIMIT 1")
    fun getLastMovementDataId(): Int

    @Query("SELECT * FROM MovementData ORDER BY id DESC LIMIT 1")
    fun getLastMovementData(): MovementData

    @Update
    fun updateMovementData(movementData: MovementData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovementData(users: MovementData)


}