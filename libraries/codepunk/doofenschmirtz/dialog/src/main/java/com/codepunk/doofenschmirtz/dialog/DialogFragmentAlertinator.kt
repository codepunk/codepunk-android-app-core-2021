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

package com.codepunk.doofenschmirtz.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.codepunk.doofenschmirtz.util.getTyped
import com.codepunk.doofenschmirtz.util.putTyped

/**
 * A generic [DialogFragment] that uses request codes and "showForResult" methods to show
 * [Dialog]s in a manner consistent with [Activity.startActivityForResult].
 */
open class DialogFragmentAlertinator :
    AppCompatDialogFragment(),
    DialogInterface.OnClickListener {

    // region Companion object

    companion object {

        // region Constants

        /**
         * A result code corresponding to no result (yet).
         */
        protected const val RESULT_NONE: Int = Int.MIN_VALUE

        /**
         * A result code corresponding to a positive button click.
         */
        const val RESULT_POSITIVE: Int = FragmentActivity.RESULT_OK

        /**
         * A result code corresponding to a negative button click.
         */
        const val RESULT_NEGATIVE: Int = FragmentActivity.RESULT_OK - 1

        /**
         * A result code corresponding to a neutral button click.
         */
        const val RESULT_NEUTRAL: Int = FragmentActivity.RESULT_OK - 2

        /**
         * A result code corresponding to the dialog being canceled.
         */
        const val RESULT_CANCELED: Int = FragmentActivity.RESULT_CANCELED

        // endregion Constants

        // region Properties

        /**
         * A base string for bundle keys used by this class.
         */
        @JvmStatic
        private val KEY_BASE = DialogFragmentAlertinator::class.java.name

        /**
         * A bundle key used to store the request code.
         */
        @JvmStatic
        private val KEY_REQUEST_CODE = "$KEY_BASE.REQUEST_CODE"

        /**
         * A bundle key used to store the OnBuildDialogListener.
         */
        @JvmStatic
        private val KEY_ON_BUILD_DIALOG_LISTENER = "$KEY_BASE.ON_BUILD_DIALOG_LISTENER"

        /**
         * A bundle key used to store the OnDialogResultListener.
         */
        @JvmStatic
        private val KEY_ON_DIALOG_RESULT_LISTENER = "$KEY_BASE.ON_DIALOG_RESULT_LISTENER"

        // endregion Properties

    }

    // endregion Companion object

    // region Properties

    /**
     * The [OnBuildDialogListener] currently listening for build dialog events. Note that
     * [DialogFragmentAlertinator] will attempt to set this automatically, both during
     * [showForResult] and after configuration change when either this fragment's targetFragment
     * or activity implements [onBuildDialogListener]. If [onBuildDialogListener] is implemented
     * by some other entity, then this listener will need to be explicitly set both prior to
     * calling [showForResult] and after configuration change.
     */
    @Suppress("WEAKER_ACCESS")
    var onBuildDialogListener: OnBuildDialogListener? = null

    /**
     * The [OnDialogResultListener] currently listening for the result from this dialog fragment.
     * Note that [DialogFragmentAlertinator] will attempt to set this automatically, both during
     * [showForResult] and after configuration change when either this fragment's targetFragment
     * or activity implements [OnDialogResultListener]. If [OnDialogResultListener] is implemented
     * by some other entity, then this listener will need to be explicitly set both prior to
     * calling [showForResult] and after configuration change.
     */
    @Suppress("WEAKER_ACCESS")
    var onDialogResultListener: OnDialogResultListener? = null

    /**
     * The request code that this fragment will use to communicate with [onBuildDialogListener].
     */
    @Suppress("WEAKER_ACCESS")
    protected var requestCode: Int = 0

    /**
     * The result code that will be passed to the listener.
     */
    @Suppress("WEAKER_ACCESS")
    protected var resultCode: Int = RESULT_NONE

    /**
     * Any data that will be shared with [onBuildDialogListener] via
     * [OnDialogResultListener.onDialogResult].
     */
    @Suppress("WEAKER_ACCESS")
    var data: Intent? = null

    // endregion Properties

    // region Lifecycle methods

    /**
     * Restores properties after configuration change.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCode = savedInstanceState?.getInt(KEY_REQUEST_CODE) ?: requestCode
        resultCode = RESULT_NONE
        savedInstanceState?.also {
            onDialogResultListener = it.getTyped(KEY_ON_DIALOG_RESULT_LISTENER, this)
            onBuildDialogListener = it.getTyped(KEY_ON_BUILD_DIALOG_LISTENER, this)
        }
    }

    /**
     * Saves request code and auto properties prior to configuration change.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REQUEST_CODE, requestCode)
        outState.putTyped(KEY_ON_DIALOG_RESULT_LISTENER, onDialogResultListener, this)
        outState.putTyped(KEY_ON_BUILD_DIALOG_LISTENER, onBuildDialogListener, this)
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Creates the dialog, allowing a [OnBuildDialogListener] to customize the builder's
     * attributes beforehand.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Builder(requireContext()).also { builder ->
            onPreBuildDialog(builder, savedInstanceState)
            onBuildDialogListener?.onPreBuildDialog(requestCode, builder, this, savedInstanceState)
        }.create()

    /**
     * Sets [resultCode] to [RESULT_CANCELED] in preparation for calling
     * [OnDialogResultListener.onDialogResult].
     */
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        resultCode = RESULT_CANCELED
    }

    /**
     * Calls [OnDialogResultListener.onDialogResult] on [onBuildDialogListener] if one exists.
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Don't notify listener if no result code has been set. This will catch the
        // difference between a dialog dismissing due to configuration change vs. a user action.
        if (resultCode != RESULT_NONE) {
            onDialogResultListener?.onDialogResult(requestCode, resultCode, data)
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Implementation of [DialogInterface.OnClickListener]. Saves the result code associated
     * with the button that was clicked.
     */
    override fun onClick(dialog: DialogInterface?, which: Int) {
        resultCode = when (which) {
            DialogInterface.BUTTON_POSITIVE -> RESULT_POSITIVE
            DialogInterface.BUTTON_NEGATIVE -> RESULT_NEGATIVE
            DialogInterface.BUTTON_NEUTRAL -> RESULT_NEUTRAL
            else -> return
        }
    }

    // endregion Implemented methods

    // region Methods

    /**
     * Provides default attributes to the supplied [builder]. This allows descendants of this
     * class to provide their own default dialog parameters.
     */
    protected open fun onPreBuildDialog(builder: Builder, savedInstanceState: Bundle?) {}

    /**
     * Shows this [DialogFragmentAlertinator] using the given [requestCode] and [tag], and the
     * [FragmentManager] supplied by the given [activity].
     */
    open fun showForResult(
        activity: FragmentActivity,
        requestCode: Int,
        tag: String
    ) {
        this.requestCode = requestCode
        onDialogResultListener = (activity as? OnDialogResultListener) ?: onDialogResultListener
        onBuildDialogListener = (activity as? OnBuildDialogListener) ?: onBuildDialogListener
        show(activity.supportFragmentManager, tag)
    }

    /**
     * Shows this [DialogFragmentAlertinator] using the given [requestCode] and [tag], and the
     * [FragmentManager] supplied by the given [fragment].
     */
    open fun showForResult(
        fragment: Fragment,
        requestCode: Int,
        tag: String
    ) {
        this.requestCode = requestCode
        onDialogResultListener = (fragment as? OnDialogResultListener) ?: onDialogResultListener
        onBuildDialogListener = (fragment as? OnBuildDialogListener) ?: onBuildDialogListener
        show(fragment.parentFragmentManager, tag)
    }

    /**
     * Shows this [DialogFragmentAlertinator] using the given [manager], [requestCode], and [tag].
     * This implementation of [showForResult] also accepts an optional [onBuildDialogListener] and
     * [onDialogResultListener]. Note that if either listener is NOT this fragment's activity or
     * target fragment, they will need to be explicitly set again after orientation change.
     */
    open fun showForResult(
        manager: FragmentManager,
        requestCode: Int,
        tag: String,
        onDialogResultListener: OnDialogResultListener? = null,
        onBuildDialogListener: OnBuildDialogListener? = null
    ) {
        this.requestCode = requestCode
        this.onDialogResultListener = onDialogResultListener
        this.onBuildDialogListener = onBuildDialogListener
        show(manager, tag)
    }

    // endregion Methods

    // region Nested/inner classes

    /**
     * An interface that classes can implement to customize an [AlertDialog.Builder] to building.
     */
    interface OnBuildDialogListener {

        // region Methods

        /**
         * Called when [DialogFragmentAlertinator] is building its [AlertDialog]. Listeners of this
         * event should use [requestCode] to differentiate what kind of alert dialog should be
         * built. When creating dialog buttons, it's best to use [onClickListener] as the
         * [DialogInterface.OnClickListener] for those buttons so the AlertDialogFragment can
         * respond appropriately and call [OnDialogResultListener.onDialogResult] with the correct
         * result code.
         */
        fun onPreBuildDialog(
            requestCode: Int,
            builder: Builder,
            onClickListener: DialogInterface.OnClickListener,
            savedInstanceState: Bundle?
        )

        // endregion Methods

    }

    /**
     * An interface that classes can implement to listen for the result sent by this
     * [DialogFragmentAlertinator]s.
     */
    interface OnDialogResultListener {

        // region Methods

        /**
         * Called when the [DialogFragmentAlertinator] encounters a result, whether from the dialog
         * being dismissed in any way, or one of the dialog buttons being pressed. Listeners of
         * this event should use [requestCode] to differentiate what kind of alert dialog is
         * passing along its result. The [data] argument may optionally contain additional
         * information (for example, an item that was selected in a list, etc.)
         */
        fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent?)

        // endregion Methods

    }

    // endregion Nested/inner classes

}
