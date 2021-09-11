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

package com.codepunk.core.ui.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codepunk.core.BuildConfig.DEV_OPTS_PASSWORD_HASH
import com.codepunk.core.BuildConfig.PREF_KEY_DEV_OPTS_AUTH_HASH
import com.codepunk.core.BuildConfig.PREF_KEY_DEV_OPTS_ENABLED
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// region Constants

/**
 * The total number of requests required to unlock developer options.
 */
private const val DEV_OPTS_REQUESTS_TO_UNLOCK: Int = 7

// endregion Constants

/**
 * The [ViewModel] used within the settings activity (and its fragments).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel(),
    OnSharedPreferenceChangeListener {

    // TODO Don't make ViewModel implement OnSharedPreferenceChangeListener. Move that to fragment.

    // region Properties

    /**
     * Private [MutableLiveData] that tracks a "you are already a developer" message.
     */
    private val _alreadyDeveloper = MutableLiveData<Lazy<Boolean>>()

    /**
     * Public [LiveData] that tracks a "you are already a developer" message.
     */
    val alreadyDeveloper: LiveData<Lazy<Boolean>>
        get() = _alreadyDeveloper

    /**
     * Private [MutableLiveData] that tracks when developer options become unlocked.
     */
    private val _devOptsUnlocked = MutableLiveData<Lazy<Boolean>>().apply {
        value = lazyOf(
            sharedPreferences.getString(PREF_KEY_DEV_OPTS_AUTH_HASH, null) != null
        )
    }

    /**
     * Public [LiveData] that tracks when developer options become unlocked.
     */
    val devOptsUnlocked: LiveData<Lazy<Boolean>>
        get() = _devOptsUnlocked

    /**
     * Private [MutableLiveData] that tracks whether developer options are enabled.
     */
    private val _devOptsEnabled = MutableLiveData<Boolean>().apply {
        value = sharedPreferences.getBoolean(PREF_KEY_DEV_OPTS_ENABLED, false)
    }

    /**
     * Public [LiveData] that tracks whether develoepr options are enabled.
     */
    val devOptsEnabled: LiveData<Boolean>
        get() = _devOptsEnabled

    /**
     * Private [MutableLiveData] that tracks how many requests remain in order to unlock
     * developer options.
     */
    private val _requestsRemaining = MutableLiveData<Lazy<Int>>().apply {
        value = lazyOf(DEV_OPTS_REQUESTS_TO_UNLOCK)
    }

    /**
     * Public [LiveData] that tracks how many requests remain in order to unlock
     * developer options.
     */
    val requestsRemaining: LiveData<Lazy<Int>>
        get() = _requestsRemaining

    // endregion Properties

    // region Constructors

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    // endregion Constructors

    // region Implemented methods

    /**
     * Implementation of [OnSharedPreferenceChangeListener]. Reacts to shared preference changes.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            PREF_KEY_DEV_OPTS_ENABLED ->
                _devOptsEnabled.value = sharedPreferences.getBoolean(key, false)
        }
    }

    // endregion Implemented methods

    // region Methods

    /**
     * Reacts to the user requesting to unlock developer options.
     */
    fun requestDevOpts(): Boolean {
        when (val remaining = _requestsRemaining.value?.value ?: 0) {
            -1 -> _alreadyDeveloper.value = lazy { true }
            0 -> _requestsRemaining.value = lazy { 0 }
            else -> _requestsRemaining.value = lazy { remaining - 1 }
        }
        return true
    }

    /**
     * Unlocks developer options if the given [hash] matches the developer options password hash.
     */
    fun unlockDevOpts(hash: String? = null) {
        val match = hash == DEV_OPTS_PASSWORD_HASH
        sharedPreferences.edit()
            .putBoolean(PREF_KEY_DEV_OPTS_ENABLED, true) /* TODO Needed? */
            .apply {
                if (match) putString(PREF_KEY_DEV_OPTS_AUTH_HASH, hash)
                else remove(PREF_KEY_DEV_OPTS_AUTH_HASH)
            }.apply()
        _devOptsUnlocked.value = lazy { match }
    }

    // endregion Methods

}
