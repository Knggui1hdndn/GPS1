package com.example.gps.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "LocationData",
    foreignKeys = [ForeignKey(
        entity = MovementData::class,
        parentColumns = ["id"],
        childColumns = ["movementDataId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class LocationData (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val latitude: Float,
    val longitude: Float,
    val movementDataId: Int
)