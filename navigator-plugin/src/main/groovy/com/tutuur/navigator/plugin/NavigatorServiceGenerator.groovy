package com.tutuur.navigator.plugin

import com.tutuur.navigator.constants.Constants
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class NavigatorServiceGenerator {

    final Collection<String> externalClasses

    final Collection<String> classes

    final File outputDirectory

    NavigatorServiceGenerator(Collection<String> externalClasses, Collection<String> classes, File outputDirectory) {
        this.externalClasses = externalClasses
        this.classes = classes
        this.outputDirectory = outputDirectory
    }

    def generate() {
        if (externalClasses.isEmpty() && classes.isEmpty()) {
            println("no intent builder class found.")
            return
        }
        def className = Constants.PATTERN_INIT_CLASS.replace('.', '/')
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {}
        visitor.visit(50, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null)
        visitor.visitEnd()
        File classFile = new File(outputDirectory, className + ".class")
        classFile.parentFile.mkdirs()
        new FileOutputStream(classFile).write(writer.toByteArray())
    }
}