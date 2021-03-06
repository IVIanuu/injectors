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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.fragment.inject
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ParentFragment : Fragment(), HasInjectors {

    @Inject override lateinit var injectors: CompositeInjectors

    @Inject lateinit var app: App
    @Inject lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context?) {
        inject()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.child_container, ChildFragment())
                .commitNow()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent, container, false)

}