/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.injectors

import javax.inject.Inject
import javax.inject.Provider

/**
 * [Injector]'s which uses [ContributeInjector] generated code
 */
class DaggerInjectors @Inject constructor(
    private val injectorFactories: Map<String, @JvmSuppressWildcards Provider<@JvmSuppressWildcards Injector.Factory<*>>>
) : Injectors {

    /**
     * Returns a [Injector] for [instance] or null
     */
    fun <T : Any> getOrNull(instance: T): Injector<T>? {
        val factoryProvider = injectorFactories[instance.javaClass.name] ?: return null
        return try {
            val factory = factoryProvider.get() as Injector.Factory<Any>
            factory.create(instance) as Injector<T>
        } catch (e: Exception) {
            null
        }

    }

    override fun <T : Any> get(instance: T) = getOrNull(instance)
        ?: throw IllegalArgumentException("no injector found for $instance")
}