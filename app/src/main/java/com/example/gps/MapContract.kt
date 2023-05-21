package com.example.gps

interface MapContract {
    interface Presenter {
        fun getLocationCurrent()
        fun getLocationChanges()
        fun stopListenerLocationChanges()
        fun resumeListenerLocationChanges()
        fun pauseListenerLocationChanges()
        fun startListenerLocationChanges()
    }
}