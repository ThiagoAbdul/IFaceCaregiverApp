package com.example.facerecognitionimages

import android.app.Application
import com.example.facerecognitionimages.di.appModule
import com.example.facerecognitionimages.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class IFaceCaregiverApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        startKoin{
            androidLogger()
            androidContext(this@IFaceCaregiverApplication)
            modules(appModule, networkModule)
        }

    }
}

