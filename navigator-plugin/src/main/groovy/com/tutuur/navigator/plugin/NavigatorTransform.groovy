package com.tutuur.navigator.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipFile

class NavigatorTransform extends Transform {

    static final String INTENT_BUILDER_CLASS_POSTFIX = "_IntentBuilder.class"

    @Override
    String getName() {
        return "Navigator"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    static def getJarContent(File file) {
        try {
            ZipFile zip = new ZipFile(file)
            return Collections.list(zip.entries()).collect({ it.name })
        } catch (IOException ignored) {
            // do nothing.
        }
    }

    @Override
    void transform(TransformInvocation invocation)
            throws TransformException, InterruptedException, IOException {
        Set<String> jarClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())
        Set<String> appClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())
        invocation.getInputs().each { input ->
            input.jarInputs.parallelStream().each { jarInput ->
                jarClasses.addAll(getJarContent(jarInput.file).findAll {
                    it.endsWith(INTENT_BUILDER_CLASS_POSTFIX)
                }.collect { it.replace(File.separatorChar, '.') })
            }
            input.directoryInputs.parallelStream().each { dirInput ->
                println(dirInput.file.absolutePath)
                appClasses.addAll(FileUtils.listFiles(dirInput.file,
                        new SuffixFileFilter(".class", IOCase.INSENSITIVE),
                        TrueFileFilter.INSTANCE).findAll { file ->
                    file.absolutePath.endsWith(INTENT_BUILDER_CLASS_POSTFIX)
                }.collect {
                    it.absolutePath
                            .substring(dirInput.file.absolutePath.length() + 1)
                            .replace(File.separatorChar, '.')
                })
            }
        }
    }
}