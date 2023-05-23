package com.example.gps.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MovementData")
class MovementData(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val start: String,
    var end: String?,
    var maxSpeed: Float,
    var averageSpeed: Float,
    var distance: Float,
    var time: Float
){
    override fun toString(): String {
        return "MovementData(id=$id, start='$start', end=$end, maxSpeed=$maxSpeed, averageSpeed=$averageSpeed, distance=$distance, time=$time)"
    }
}