package com.tutuur.navigator.plugin

import com.tutuur.navigator.constants.Constants
import org.objectweb.asm.*

class NavigatorServiceGenerator {

    final Collection<String> classesInDependencies

    final Collection<String> classesInModule

    final File outputDirectory

    NavigatorServiceGenerator(Collection<String> classesInDependencies, Collection<String> classesInModule, File outputDirectory) {
        this.classesInDependencies = classesInDependencies
        this.classesInModule = classesInModule
        this.outputDirectory = outputDirectory
    }

    def generate() {
        if (classesInDependencies.isEmpty() && classesInModule.isEmpty()) {
            println("no intent builder class found.")
            return
        }
        def className = Constants.PATTERN_INIT_CLASS.replace('.', '/')
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
        writer.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null)
        String signature = "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/reflect/Constructor<+Lcom.tutuur.navigator.IntentBuilder;>;>;)V"
        String[] exceptions = ["java/lang/NoSuchMethodException"]
        MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "init", "(Ljava/util/Map;)V", signature, exceptions)
        mv.visitCode()
        (classesInDependencies + classesInModule).each {
            Label end = new Label()
            // if (Builder.PAGE != null) {
            // stack: PAGE
            mv.visitFieldInsn(Opcodes.GETSTATIC, it, "PAGE", "Ljava/lang/String;")
            mv.visitJumpInsn(Opcodes.IFNULL, end)

            // map.put(page, ...)
            // stack: arg0
            mv.visitVarInsn(Opcodes.ALOAD, 0)

            // stack: arg0, PAGE
            mv.visitFieldInsn(Opcodes.GETSTATIC, it, "PAGE", "Ljava/lang/String;")

            // stack: arg0, PAGE, class
            mv.visitLdcInsn(Type.getType("L$it;"))

            // stack: arg0, PAGE, class, 0
            mv.visitInsn(Opcodes.ICONST_0)

            // stack: arg0, PAGE, class, class[]
            mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class")

            // stack: arg0, PAGE, Ctor.
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false)

            // stack:
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false)
            mv.visitInsn(Opcodes.POP)

            // }
            mv.visitLabel(end)
        }
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
        writer.visitEnd()
        File classFile = new File(outputDirectory, className + ".class")
        classFile.parentFile.mkdirs()
        new FileOutputStream(classFile).write(writer.toByteArray())
    }
}