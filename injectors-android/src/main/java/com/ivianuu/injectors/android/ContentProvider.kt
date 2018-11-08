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

package com.ivianuu.injectors.android

import android.app.Service
import android.content.ContentProvider
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.inject

private val INJECTORS_FINDER: (ContentProvider) -> HasInjectors? = {
    it.context!!.applicationContext as? HasInjectors
}

/**
 * Injects the [instance]
 */
fun Injector.Companion.inject(instance: ContentProvider) {
    inject(instance, INJECTORS_FINDER)
}

/**
 * Injects [this]
 */
fun ContentProvider.inject() = Injector.inject(this)