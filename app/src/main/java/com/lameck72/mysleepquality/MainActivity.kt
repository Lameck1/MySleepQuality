package com.lameck72.mysleepquality

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lameck72.mysleepquality.R


/**
 * The MySleepQuality app is a demo app that helps you collect information about your sleep.
 * - Start time, end time, quality, and time slept
 *
 * This app demonstrates the following views and techniques:
 * - Room database, DAO, and Coroutines
 *
 * It also uses and builds on the following techniques:
 * - Transformation map
 * - Data Binding in XML files
 * - ViewModel Factory
 * - Using Backing Properties to protect MutableLiveData
 * - Observable state LiveData variables to trigger navigation
 */

/**
 * This main activity is just a container for fragments,
 * where the real action is.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}
