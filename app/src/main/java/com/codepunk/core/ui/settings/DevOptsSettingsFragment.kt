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

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import com.codepunk.core.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * A preference fragment that displays developer options preferences to the user. By default,
 * developer options are not available to the user until they unlock the developer options
 * preference and openSession themselves as a developer.
 */
@AndroidEntryPoint
class DevOptsSettingsFragment() : PreferenceFragmentCompat() {

    // region Properties

    /**
     * The [SettingsViewModel] instance backing this fragment.
     */
    private val viewModel: SettingsViewModel by viewModels()

    // endregion Properties

    // region Lifecycle methods

    /**
     * Updates the activity title.
     */
    override fun onStart() {
        super.onStart()
        requireActivity().title = preferenceScreen.title
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Creates and initializes preferences.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.dev_opts_settings, rootKey)
    }

    // endregion Inherited methods

}
