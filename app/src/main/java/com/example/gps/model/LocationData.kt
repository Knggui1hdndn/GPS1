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
    val latitude: Double,
    val longitude: Double,
    val movementDataId: Int
){
    override fun toString(): String {
        return "LocationData(id=$id, latitude=$latitude, longitude=$longitude, movementDataId=$movementDataId)"
    }
}