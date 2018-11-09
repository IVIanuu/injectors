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

package com.ivianuu.injectors.compiler

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.google.common.base.Joiner
import java.io.IOException
import java.io.Writer
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.tools.StandardLocation

@AutoService(Processor::class)
class InjectorsProcessor : BasicAnnotationProcessor() {

    override fun initSteps() =
        mutableSetOf(ContributeInjectorProcessingStep(processingEnv))

    override fun postRound(roundEnv: RoundEnvironment) {
        if (roundEnv.processingOver()) {
            try {
                createProguardFile().use { writer ->
                    writer.write(
                        Joiner.on("\n")
                            .join(
                                "-identifiernamestring class dagger.android.internal.AndroidInjectionKeys {",
                                "  java.lang.String of(java.lang.String);",
                                "}"
                            )
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createProguardFile(): Writer = processingEnv
        .filer
        .createResource(
            StandardLocation.CLASS_OUTPUT,
            "",
            "META-INF/proguard/com.ivianuu.injectors.InjectionKeys"
        )
        .openWriter()

}