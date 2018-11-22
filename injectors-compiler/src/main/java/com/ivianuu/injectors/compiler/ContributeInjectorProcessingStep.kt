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
import com.google.common.collect.SetMultimap
import com.ivianuu.injectors.ContributesInjector
import com.ivianuu.processingx.ProcessingEnvHolder
import com.ivianuu.processingx.ProcessingStep
import com.ivianuu.processingx.asJavaClassName
import com.ivianuu.processingx.asJavaTypeName
import com.ivianuu.processingx.elementUtils
import com.ivianuu.processingx.filer
import com.ivianuu.processingx.getAnnotatedAnnotations
import com.ivianuu.processingx.getAnnotationMirror
import com.ivianuu.processingx.getAsTypeList
import com.ivianuu.processingx.hasAnnotation
import com.ivianuu.processingx.messager
import com.ivianuu.processingx.validateAll
import com.squareup.javapoet.ClassName
import dagger.Module
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ContributeInjectorProcessingStep : ProcessingStep, ProcessingEnvHolder {

    override lateinit var processingEnv: ProcessingEnvironment

    private val contributionModules = mutableSetOf<ContributionsModuleDescriptor>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.processingEnv = processingEnv
    }

    override fun annotations() =
        setOf(ContributesInjector::class.java)

    override fun validate(annotationClass: Class<out Annotation>, element: Element) =
    // we only need to know about the annotations
        element.annotationMirrors.validateAll()

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        val descriptors = elementsByAnnotation[ContributesInjector::class.java]
            .asSequence()
            .filterIsInstance<ExecutableElement>()
            .mapNotNull { createContributeInjectorDescriptor(it) }
            .toList()

        descriptors
            .map { ContributeInjectorGenerator(it) }
            .map { it.generate() }
            .forEach { it.writeTo(filer) }

        createContributesModuleDescriptors(descriptors)
            .onEach { contributionModules.add(it) }
            .map { ContributionsModuleGenerator(it) }
            .map { it.generate() }
            .forEach { it.writeTo(filer) }

        return mutableSetOf()
    }

    override fun postRound(processingOver: Boolean) {
        if (!processingOver) return

        contributionModules.forEach {
            val moduleElement = elementUtils.getTypeElement(it.target.toString())

            val moduleAnnotation = moduleElement.getAnnotationMirror<Module>()
            val includes = moduleAnnotation.getAsTypeList("includes")

            if (!includes.map { it.toString() }.contains(it.moduleName.toString())) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@ContributesInjector modules must include the generated module",
                    moduleElement
                )
            }
        }
    }

    private fun createContributeInjectorDescriptor(element: ExecutableElement): ContributeInjectorDescriptor? {
        if (!element.enclosingElement.hasAnnotation<Module>()) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "@ContributesInjector must be in @Module class"
            )
            return null
        }

        val enclosingModule = (element.enclosingElement as TypeElement).asJavaClassName()

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

        val target = element.returnType.asJavaTypeName() as ClassName

        val baseName = target.simpleName()
        val subcomponentName = moduleName.nestedClass(baseName + "Subcomponent")
        val subcomponentBuilderName = subcomponentName.nestedClass("Builder")

        val scopes = element.getAnnotatedAnnotations<Scope>()

        val annotation =
            element.getAnnotationMirror<ContributesInjector>()

        val modules = annotation.getAsTypeList("modules")
            .map { elementUtils.getTypeElement(it.toString()) }
            .map { it.asJavaClassName() }
            .toSet()

        return ContributeInjectorDescriptor(
            element,
            target,
            moduleName,
            modules,
            scopes,
            subcomponentName,
            subcomponentBuilderName
        )
    }

    private fun createContributesModuleDescriptors(contributions: List<ContributeInjectorDescriptor>): List<ContributionsModuleDescriptor> {
        val byModule = contributions.groupBy {
            it.element.enclosingElement as TypeElement
        }

        return byModule.map { (module, descriptors) ->
            val contributionsName =
                ClassName.bestGuess(module.qualifiedName.toString() + "_Contributions")
            ContributionsModuleDescriptor(
                module.asJavaClassName(),
                contributionsName,
                module.modifiers.contains(Modifier.PUBLIC),
                descriptors.toSet()
            )
        }
    }
}