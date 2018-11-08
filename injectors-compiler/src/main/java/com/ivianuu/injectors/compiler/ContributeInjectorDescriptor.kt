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

import com.google.common.base.CaseFormat
import com.google.common.base.Joiner
import com.squareup.javapoet.ClassName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

// todo remove builder
data class ContributeInjectorDescriptor(
    val element: ExecutableElement,
    val target: ClassName,
    val moduleName: ClassName,
    val modules: Set<ClassName>,
    val scopes: Set<AnnotationMirror>,
    val subcomponentName: ClassName,
    val subcomponentBuilderName: ClassName
) {

    class Builder internal constructor(
        val element: ExecutableElement,
        val target: ClassName,
        val moduleName: ClassName,
        val subcomponentName: ClassName,
        val subcomponenBuilderName: ClassName
    ) {

        private val modules = mutableSetOf<ClassName>()
        private val scopes = mutableSetOf<AnnotationMirror>()

        fun addModule(module: ClassName): Builder {
            modules.add(module)
            return this
        }

        fun addScope(scope: AnnotationMirror): Builder {
            scopes.add(scope)
            return this
        }

        fun build(): ContributeInjectorDescriptor {
            return ContributeInjectorDescriptor(
                element,
                target,
                moduleName,
                modules,
                scopes,
                subcomponentName,
                subcomponenBuilderName
            )
        }

    }

    companion object {

        fun builder(
            element: ExecutableElement
        ): Builder {
            val enclosingModule = ClassName.get(element.enclosingElement as TypeElement)

            val moduleName = enclosingModule
                .topLevelClassName()
                .peerClass(
                    Joiner.on('_').join(enclosingModule.simpleNames())
                            + "_"
                            + CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.UPPER_CAMEL,
                        element.simpleName.toString()
                    )
                )

            val target = ClassName.bestGuess(element.returnType.toString())

            val baseName = target.simpleName()
            val subcomponentName = moduleName.nestedClass(baseName + "Subcomponent")
            val subcomponenBuilderName = subcomponentName.nestedClass("Builder")

            return Builder(
                element,
                target,
                moduleName,
                subcomponentName,
                subcomponenBuilderName
            )
        }

    }

}