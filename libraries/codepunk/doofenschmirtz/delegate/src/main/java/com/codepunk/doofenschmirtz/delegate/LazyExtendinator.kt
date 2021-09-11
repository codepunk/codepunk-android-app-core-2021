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

package com.codepunk.doofenschmirtz.delegate

/**
 * Calls [onConsume] if this [Lazy] instance is uninitialized, and returns the encapsulated value.
 */
@Suppress("UNUSED")
fun <T> Lazy<T>.consume(onConsume: (T) -> Unit): T =
    if (wasInitialized()) value else value.also(onConsume)

/**
 * Returns the encapsulated value if it has been initialized or [defaultValue] otherwise. Note that
 * this [Lazy] instance's initialized state will remain unchanged.
 */
@Suppress("UNUSED")
fun <T> Lazy<T>.getOrDefault(defaultValue: T): T = if (isInitialized()) value else defaultValue

/**
 * Returns the encapsulated value if it has been initialized or the result of [defaultValue]
 * function otherwise. Note that this [Lazy] instance's initialized state will remain unchanged
 * (unless [Lazy.value] is accessed as a result of invoking [defaultValue]).
 */
@Suppress("UNUSED")
fun <T> Lazy<T>.getOrElse(defaultValue: () -> T): T = if (isInitialized()) value else defaultValue()

/**
 * Returns the encapsulated value if it has been initialized or null otherwise. Note that this
 * [Lazy] instance's initialized state will remain unchanged.
 */
@Suppress("UNUSED")
fun <T> Lazy<T>.getOrNull(): T? = if (isInitialized()) value else null

/**
 * A thread-safe way of determining whether this [Lazy] instance was initialized, but also forcing
 * initialization to occur if not.
 */
@Suppress("UNUSED")
fun <T> Lazy<T>.wasInitialized(): Boolean = synchronized(this) { isInitialized().also { value } }
