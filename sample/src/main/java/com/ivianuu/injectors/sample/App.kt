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

import android.app.Application
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.InjectorModule
import dagger.Component
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Manuel Wrage (IVIanuu)
 */
class App : Application(), HasInjectors {

    @Inject override lateinit var injectors: CompositeInjectors

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .create(this)
            .inject(this)
    }

}

@Singleton
@Component(
    modules = [
        ActivityBindingModule_Contributions::class,
        InjectorModule::class,
        ServiceBindingModule_Contributions::class
    ]
)
interface AppComponent : Injector<App> {
    @Component.Builder
    abstract class Builder : Injector.Builder<App>()
}