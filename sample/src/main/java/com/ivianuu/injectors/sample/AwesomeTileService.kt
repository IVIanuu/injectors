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

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.ivianuu.injectors.DaggerInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.inject
import com.ivianuu.injectors.android.inject
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@RequiresApi(Build.VERSION_CODES.N)
class AwesomeTileService : TileService(), HasInjectors {

    @Inject override lateinit var injectors: DaggerInjectors

    @Inject lateinit var app: App

    override fun onCreate() {
        inject()
        super.onCreate()
    }
}