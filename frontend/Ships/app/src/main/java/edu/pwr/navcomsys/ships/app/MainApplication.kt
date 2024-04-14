package edu.pwr.navcomsys.ships.app

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppKoin.init(this)
    }
}