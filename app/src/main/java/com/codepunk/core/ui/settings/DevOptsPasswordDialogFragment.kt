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

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.codepunk.core.BuildConfig
import com.codepunk.core.R
import com.codepunk.core.databinding.DevOptsPasswordDialogBinding
import com.codepunk.core.di.module.SHAKE_QUALIFIER
import com.codepunk.core.di.scope.FragmentScope
import com.codepunk.doofenschmirtz.dialog.DialogFragmentAlertinator
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Named

/**
 * Dialog fragment used to get the developer password from the user. The developer password is not
 * stored anywhere in the app; rather, the SHA-256 hashed password is stored and this class hashes
 * the user's input in order to compare it with the stored hash.
 */
@AndroidEntryPoint
class DevOptsPasswordDialogFragment @Inject constructor() :
    DialogFragmentAlertinator(),
    OnShowListener,
    OnClickListener {

    // region Properties

    /**
     * The application [SharedPreferences].
     */
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /**
     * An instance of [DigestUtils] for performing [MessageDigest] tasks.
     */
    @Inject
    @FragmentScope
    lateinit var digestUtils: DigestUtils

    /**
     * An [Animator] for displaying a subtle "shake" animation when the user enters an
     * incorrect password.
     */
    @Inject
    @FragmentScope
    @Named(SHAKE_QUALIFIER)
    lateinit var shakeAnimator: Animator

    /**
     * The binding for this fragment.
     */
    private lateinit var binding: DevOptsPasswordDialogBinding

    /**
     * The [Button] that represents a positive response by the user.
     */
    private val positiveBtn by lazy { (dialog as AlertDialog).getButton(BUTTON_POSITIVE) }

    // endregion Properties

    // region Lifecycle methods

    /**
     * Inflates the view and sets up binding.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dev_opts_password_dialog,
            null,
            false
        )
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Creates the developer options password dialog.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener(this@DevOptsPasswordDialogFragment)
            view?.run { shakeAnimator.setTarget(this) }
        }

    /**
     * Supplies the attributes necessary to build the Developer Options Password dialog.
     */
    override fun onPreBuildDialog(builder: AlertDialog.Builder, savedInstanceState: Bundle?) {
        builder
            .setTitle(R.string.settings_dev_opts_password_dialog_title)
            .setMessage(R.string.settings_dev_opts_password_dialog_message)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, this)
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Implementation of [DialogInterface.OnShowListener]. Sets the positive button's
     * OnClickListener when the dialog is shown so we can perform custom logic (i.e. check the
     * password entered by the user)
     */
    override fun onShow(dialog: DialogInterface?) {
        positiveBtn.setOnClickListener(this)
    }

    /**
     * Implementation of [View.OnClickListener]. Tests the password entered by the user against the
     * stored hashed password, and shake the dialog if the user enters the incorrect password.
     */
    override fun onClick(v: View?) {
        when (v) {
            positiveBtn -> {
                val password = binding.edit.text.toString()
                val hex = String(Hex.encodeHex(digestUtils.digest(password)))
                if (BuildConfig.DEV_OPTS_PASSWORD_HASH.equals(hex, true)) {
                    resultCode = RESULT_POSITIVE
                    data = Intent().apply {
                        putExtra(
                            BuildConfig.EXTRA_DEV_OPTS_PASSWORD_HASH,
                            BuildConfig.DEV_OPTS_PASSWORD_HASH
                        )
                    }
                    dialog?.dismiss()
                } else {
                    binding.layout.error = getString(R.string.settings_dev_opts_incorrect_password)
                    shakeAnimator.start()
                }
            }
        }
    }

    // endregion Implemented methods

}