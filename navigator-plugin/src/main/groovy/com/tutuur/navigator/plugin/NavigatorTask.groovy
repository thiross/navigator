package com.tutuur.navigator.plugin

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.internal.file.UnionFileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipFile

import static com.android.build.gradle.internal.publishing.AndroidArtifacts.ARTIFACT_TYPE
import static com.tutuur.navigator.constants.Constants.BUNDLE_BUILDER_CLASS_SUFFIX

@CacheableTask
class NavigatorTask extends DefaultTask {

    BaseVariant variant

    File generateOutputFold

    @InputFiles
    @Classpath
    def getJarDependencies() {
        Action<AttributeContainer> attributes = { container ->
            container.attribute(ARTIFACT_TYPE, AndroidArtifacts.ArtifactType.CLASSES.type)
        }
        variant.compileConfiguration
                .incoming
                .artifactView({ it.attributes(attributes) })
                .artifacts
                .artifactFiles
    }

    static def getJarContent(File file) {
        try {
            ZipFile zip = new ZipFile(file)
            return Collections.list(zip.entries()).collect({ it.name })
        } catch (IOException e) {
            // do nothing.
        }
    }

    @TaskAction
    def generateServiceLoader() {
        final jarBuilderClasses = getJarDependencies().collectMany { dependency ->
            if (!dependency.getName().endsWith(".jar")) {
                return []
            }
            if (!dependency.exists()) {
                logger.debug("${dependency.absolutePath} doesn't exist")
                return []
            }
            return getJarContent(dependency.absoluteFile).findAll {
                it.endsWith(BUNDLE_BUILDER_CLASS_SUFFIX)
            }
        }
        final localBuilderClasses = new UnionFileCollection(variant.javaCompiler.source, project.fileTree(generateOutputFold))
                .collect { it.absolutePath }
                .findAll { it.endsWith(BUNDLE_BUILDER_CLASS_SUFFIX) }
        jarBuilderClasses.each { println(it) }
        localBuilderClasses.each { println(it) }
    }
}
