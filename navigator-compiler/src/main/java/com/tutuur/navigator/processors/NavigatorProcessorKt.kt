package com.tutuur.navigator.processors

import com.squareup.javapoet.JavaFile
import com.tutuur.compiler.extensions.e
import com.tutuur.compiler.extensions.w
import com.tutuur.navigator.BundleExtra
import com.tutuur.navigator.Navigation
import com.tutuur.navigator.generators.IntentBuilderGenerator
import com.tutuur.navigator.models.NavigationTarget
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class NavigatorProcessorKt : AbstractProcessor() {

    private lateinit var env: ProcessingEnvironment

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes() = setOf(
            Navigation::class.java.canonicalName,
            BundleExtra::class.java.canonicalName
    )

    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        this.env = env
    }

    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {
        val navigations =
                roundEnvironment.getElementsAnnotatedWith(Navigation::class.java) +
                        roundEnvironment.getElementsAnnotatedWith(BundleExtra::class.java).map {
                            it.enclosingElement
                        }
        navigations.asSequence()
                .map { NavigationTarget(it as TypeElement) }
                .distinct()
                .map { IntentBuilderGenerator(it, env) }.toList()
                .forEach { createFile(it.brewJava()) }
        return false
    }

    private fun createFile(file: JavaFile?) {
        file?.let {
            try {
                file.writeTo(env.filer)
            } catch (e: IOException) {
                env.e(TAG, e.message!!)
            }
        }
    }

    companion object {
        val TAG = NavigatorProcessorKt::class.java.simpleName!!
    }
}