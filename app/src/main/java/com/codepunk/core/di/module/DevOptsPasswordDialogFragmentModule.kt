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

package com.codepunk.core.di.module

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import com.codepunk.core.R
import com.codepunk.core.ui.settings.DevOptsPasswordDialogFragment
import com.codepunk.doofenschmirtz.view.animation.ShakeInterpolator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import javax.inject.Named

/**
 * A constant representing the named qualifier used to inject a shake animator via
 * [DevOptsPasswordDialogFragmentModule.providesShakeAnimator].
 */
const val SHAKE_QUALIFIER = "Shake"

/**
 * A [Module] for injecting dependencies into [DevOptsPasswordDialogFragment].
 */
@Module
@InstallIn(FragmentComponent::class)
object DevOptsPasswordDialogFragmentModule {

    // region Methods

    /**
     * Provides an instance of [DigestUtils].
     */
    @Provides
    fun providesDigestUtils(): DigestUtils = DigestUtils(MessageDigestAlgorithms.SHA_256)

    /**
     * Provides an instance of [ShakeInterpolator].
     */
    @Provides
    fun providesShakeInterpolator() = ShakeInterpolator()

    /**
     * Provides an instance of [Animator] for shake animations.
     */
    @Provides
        @Named(value = SHAKE_QUALIFIER)
    fun providesShakeAnimator(
        @ActivityContext context: Context,
        shakeInterpolator: ShakeInterpolator
    ): Animator = AnimatorInflater.loadAnimator(context, R.animator.shake).apply {
        interpolator = shakeInterpolator
    }

    // endregion Methods

}