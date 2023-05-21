package com.example.gps.utils

import java.util.Locale

class TimeUtils {
   companion object{
       fun formatTime(timeInMillis: Long): String {
           val seconds = (timeInMillis / 1000).toInt() % 60
           val minutes = (timeInMillis / (1000 * 60) % 60).toInt()
           val hours = (timeInMillis / (1000 * 60 * 60) % 24).toInt()
           return String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds)
       }
   }
}