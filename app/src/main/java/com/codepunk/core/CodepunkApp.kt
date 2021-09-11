/*
 * Copyright (C) 2021 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.core

import android.app.Application
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * The Codepunk [Application] class.
 */
@HiltAndroidApp
class CodepunkApp :
    Application(),
    OnSharedPreferenceChangeListener {

    // region Properties

    /**
     * The application [SharedPreferences].
     */
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    // endregion Properties

    // region Lifecycle methods

    /**
     * Establishes the environment for API calls.
     */
    override fun onCreate() {
        super.onCreate()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        PreferenceManager.setDefaultValues(this, R.xml.main_settings, false)
        // [add additional settings xmls here as they are created]
    }

    /**
     * Performs cleanup. Not totally necessary since the app is done when the Application object
     * is destroyed, but is it included here because it's generally good programming practice to
     * include one call to unregisterXYZ for every call to registerXYZ.
     */
    override fun onTerminate() {
        super.onTerminate()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    // endregion Lifecycle methods

    // region Implemented methods

    /**
     * Implementation of [OnSharedPreferenceChangeListener]. Updates URL override interceptor
     * if necessary.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // TODO Respond to shared preference changes here
    }

    // endregion Implemented methods

}
