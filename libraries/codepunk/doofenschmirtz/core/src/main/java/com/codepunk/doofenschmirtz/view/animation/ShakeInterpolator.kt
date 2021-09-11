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

package com.codepunk.doofenschmirtz.view.animation

import android.animation.TimeInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.Interpolator

// region Constants

/**
 * The default frequency used by [ShakeInterpolator].
 */
private const val DEFAULT_FREQUENCY: Int = 5

// endregion Constants

/**
 * An interpolator used for animating a shake or "wobble"-type animation. This interpolator
 * combines the product of two [CycleInterpolator]s in order to produce an oscillating effect
 * whose amplitude grows and then shrinks in intensity over the duration of the animation. The
 * shake effect will occur the number of times specified by frequency, while a custom
 * [amplitudeInterpolator] can be supplied to control how the intensity of the shake effect
 * unfolds over the duration of the animation.
 */
class ShakeInterpolator(
    frequency: Int = DEFAULT_FREQUENCY,
    private val amplitudeInterpolator: Interpolator? = CycleInterpolator(0.5f)
) : TimeInterpolator {

    // region Properties

    /**
     * The [CycleInterpolator] that controls the primary shake effect. It is controlled by the
     * frequency parameter supplied when creating the interpolator.
     */
    private val cycleInterpolator = CycleInterpolator(frequency.toFloat())

    // endregion Properties

    // region Inherited methods

    /**
     * Maps a value representing the elapsed fraction of an animation to a value that represents
     * the interpolated fraction. This interpolated value is then multiplied by the change in value
     * of an animation to derive the animated value at the current elapsed animation time.
     */
    override fun getInterpolation(input: Float): Float =
        cycleInterpolator.getInterpolation(input) *
            (amplitudeInterpolator?.getInterpolation(input) ?: 1.0f)

    // endregion Inherited methods

}
