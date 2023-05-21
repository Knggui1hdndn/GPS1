package com.example.gps.dao

import android.content.Context
import android.location.Location
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gps.model.LocationData
import com.example.gps.model.MovementData

@Database(entities = [MovementData::class,LocationData::class], version = 1, exportSchema = false)
abstract class MyDataBase : RoomDatabase() {
    abstract fun movementDao():MovementDataDao
    abstract fun locationDao():LocationDao
    companion object {
        @Volatile
        private var INSTANCE: MyDataBase? = null
        fun getInstance(context: Context): MyDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDataBase::class.java,
                    "app_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }

        }

    }

}