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

import dagger.BindsInstance

/**
 * Performs injection on a specific type
 */
interface Injector<T : Any> {

    /**
     * Injects [instance]
     */
    fun inject(instance: T)

    /**
     * A factory for [Injector]'s
     */
    interface Factory<T : Any> {
        /**
         * Returns a new [Injector] for [instance]
         */
        fun create(instance: T): Injector<T>
    }

    /**
     * A builder for [Injector]'s
     */
    abstract class Builder<T : Any> : Factory<T> {
        
        override fun create(instance: T): Injector<T> {
            seedInstance(instance)
            return build()
        }

        /**
         * Seeds the [instance]
         */
        @BindsInstance
        abstract fun seedInstance(instance: T)

        /**
         * Builds the [Injector]
         */
        abstract fun build(): Injector<T>
    }

    companion object

}

/**
 * Injects the [instance] or throws
 */
fun <T : Any> Injector.Companion.inject(instance: T, injectorsFinder: (T) -> HasInjectors?) {
    val hasInjectorStore = injectorsFinder(instance)
        ?: throw IllegalStateException("no injector found for ${instance.javaClass.name}")
    val injectorStore = hasInjectorStore.injectors
    val injector = injectorStore[instance]
    injector.inject(instance)
}

fun <T : Any> T.inject(injectorsFinder: (T) -> HasInjectors?) =
    Injector.inject(this, injectorsFinder)