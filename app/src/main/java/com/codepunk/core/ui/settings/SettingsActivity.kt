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

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.codepunk.core.R
import com.codepunk.core.databinding.SettingsActivityBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * The [Activity] that will serve as the container for all settings-related fragments.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    // region Properties

    /**
     * The binding for this activity.
     */
    private lateinit var binding: SettingsActivityBinding

    /**
     * The navigation controller for the activity.
     */
    private val navController: NavController by lazy { findNavController(R.id.nav_fragment) }

    // endregion Properties

    // region Lifecycle methods

    /**
     * Injects dependencies into, and inflates, the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Finishes the activity if the up button is clicked when we're already at the top of the
     * navigation stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        if (!navController.navigateUp()) {
            finish()
        }
        return true
    }

    // endregion Inherited methods

}
