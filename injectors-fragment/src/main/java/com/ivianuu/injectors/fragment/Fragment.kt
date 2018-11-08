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

package com.ivianuu.injectors.fragment

import android.app.Activity
import androidx.fragment.app.Fragment
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.inject

private val INJECTORS_FINDER: (Fragment) -> HasInjectors? = {
    var hasInjectors: HasInjectors? = null

    var parentFragment: Fragment? = it.parentFragment

    // loop trough parents
    while (parentFragment != null) {
        if (parentFragment is HasInjectors) {
            hasInjectors = parentFragment
            break
        }

        parentFragment = parentFragment.parentFragment
    }

    // check activity
    if (hasInjectors == null) {
        hasInjectors = it.activity as? HasInjectors
    }

    // check application
    if (hasInjectors == null) {
        hasInjectors = it.activity?.application as? HasInjectors
    }

    hasInjectors
}

/**
 * Injects the [instance]
 */
fun Injector.Companion.inject(instance: Fragment) {
    inject(instance, INJECTORS_FINDER)
}

/**
 * Injects [this]
 */
fun Fragment.inject() = Injector.inject(this)