package com.tutuur.navigator.plugin

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.tools.r8.com.google.common.collect.ImmutableSet
import com.tutuur.navigator.constants.Constants
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipFile

class NavigatorTransform extends Transform {

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

    static def getClassFromPath(String path, String prefix = null) {
        int start = prefix == null ? 0 : prefix.length() + 1
        return path.substring(start, path.length() - SdkConstants.DOT_CLASS.length())
                .replace(File.separatorChar, '.' as char)
                .replace('.', '/')
    }

    @Override
    void transform(TransformInvocation invocation)
            throws TransformException, InterruptedException, IOException {
        final suffix = Constants.INTENT_BUILDER_CLASS_SUFFIX + SdkConstants.DOT_CLASS
        Set<String> jarClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())
        Set<String> appClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())
        invocation.getInputs().each { input ->
            input.jarInputs.parallelStream().each { jarInput ->
                File jarOutputDirectory = invocation.outputProvider
                        .getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, jarOutputDirectory)
                jarClasses.addAll(getJarContent(jarInput.file).findAll {
                    it.endsWith(suffix)
                }.collect { getClassFromPath(it) })
            }
            input.directoryInputs.parallelStream().each { dirInput ->
                File classOutputDirectory = invocation.outputProvider
                        .getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, classOutputDirectory)
                appClasses.addAll(FileUtils.listFiles(dirInput.file,
                        new SuffixFileFilter(SdkConstants.DOT_CLASS, IOCase.INSENSITIVE),
                        TrueFileFilter.INSTANCE).findAll { file ->
                    file.absolutePath.endsWith(suffix)
                }.collect { getClassFromPath(it.absolutePath, dirInput.file.absolutePath) })
            }
        }
        final outputDirectory = invocation.outputProvider
                .getContentLocation(name, TransformManager.CONTENT_CLASS, ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY)
        new NavigatorServiceGenerator(jarClasses, appClasses, outputDirectory)
                .generate()
    }
}