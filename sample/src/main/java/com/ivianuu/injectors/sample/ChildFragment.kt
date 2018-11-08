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

package com.ivianuu.injectors.sample

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ivianuu.injectors.DaggerInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injectors
import com.ivianuu.injectors.fragment.inject
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ChildFragment : Fragment(), HasInjectors {

    @Inject override lateinit var injectors: DaggerInjectors

    @Inject lateinit var app: App
    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var parentFragment: ParentFragment

    override fun onAttach(context: Context?) {
        inject()
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = MyView(HasInjectorsContextWrapper(requireActivity(), this))

    private class HasInjectorsContextWrapper(base: Context, hasInjectors: HasInjectors)
        : ContextWrapper(base), HasInjectors by hasInjectors

}