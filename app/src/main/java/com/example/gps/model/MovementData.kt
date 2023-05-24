package com.example.gps.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "MovementData")
class MovementData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Long,
    val startLatitude: Double,
    var startLongitude: Double,
    var endLatitude: Double,
    var endLongitude: Double,
    var maxSpeed: Float,
    var averageSpeed: Float,
    var distance: Float,
    var time: Float
) :Serializable{

}