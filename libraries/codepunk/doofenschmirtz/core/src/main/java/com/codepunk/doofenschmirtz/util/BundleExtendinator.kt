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

package com.codepunk.doofenschmirtz.util

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Enumerated class describing a value in relation to a [Fragment].
 */
private enum class ValueType {

    /**
     * The [ValueType] associated with a value that is a fragment's activity.
     */
    ACTIVITY,

    /**
     * The [ValueType] associated with a value that is a fragment's parent fragment.
     */
    PARENT_FRAGMENT,

    /**
     * The [ValueType] associated with a value that is a fragment's targetFragment.
     */
    TARGET_FRAGMENT,

    /**
     * The [ValueType] associated with a value that is neither a fragment's activity nor its
     * targetFragment.
     */
    OTHER
}

/**
 * Convenience method that inserts the [ValueType] associated with [value] into this [Bundle].
 * The ValueType describes whether [value] is [fragment]'s activity, [fragment]'s targetFragment,
 * or some other value.
 */
fun <T> Bundle.putTyped(key: String?, value: T?, fragment: Fragment) {
    putSerializable(
        key,
        when (value) {
            fragment.activity -> ValueType.ACTIVITY
            fragment.parentFragment -> ValueType.PARENT_FRAGMENT
            fragment.targetFragment -> ValueType.TARGET_FRAGMENT
            else -> ValueType.OTHER
        }
    )
}

/**
 * Convenience method that resolves the value of a [ValueType] inserted into this [Bundle] via
 * [putTyped]. The result will be one of: [fragment]'s activity, [fragment]'s targetFragment,
 * or null.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Bundle.getTyped(key: String?, fragment: Fragment, defaultValue: T? = null): T? =
    when (getSerializable(key)) {
        ValueType.ACTIVITY -> fragment.activity as T?
        ValueType.PARENT_FRAGMENT -> fragment.parentFragment as T?
        ValueType.TARGET_FRAGMENT -> fragment.targetFragment as T?
        else -> defaultValue
    }
