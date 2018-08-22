package com.tutuur.util;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * {@code AnnotationProcessor} utilities.
 *
 * @author yujie
 */
@SuppressWarnings("ALL")
public class AnnotationProcessorHelper {

    private static final String MESSAGE_FORMAT = "[%s] %s";

    private final ProcessingEnvironment env;

    private final boolean debug;

    /**
     * Create instance of this utitlity.
     *
     * @param env   AnnotationProcessor environment.
     * @param debug More logs when set true.
     */
    public AnnotationProcessorHelper(ProcessingEnvironment env, boolean debug) {
        this.env = env;
        this.debug = debug;
    }

    public ProcessingEnvironment getEnv() {
        return env;
    }

    public String getPackageName(TypeElement element) {
        return env.getElementUtils()
                .getPackageOf(element)
                .getQualifiedName()
                .toString();
    }

    public boolean isString(TypeMirror type) {
        return isSameType(type, TypeConstants.FQDN_STRING);
    }

    private boolean isArrayOf(TypeMirror type, TypeMirror elementType) {
        final Types types = env.getTypeUtils();
        return types.isAssignable(type, types.getArrayType(elementType));
    }

    public boolean isStringArray(TypeMirror type) {
        return isArrayOf(type, ofType(TypeConstants.FQDN_STRING));
    }

    public boolean isParcelableArray(TypeMirror type) {
        return isArrayOf(type, ofType(TypeConstants.FQDN_PARCELABLE));
    }

    private boolean isSingleParameterizedType(TypeMirror type, TypeMirror parameterType, TypeMirror genericType) {
        if (!(type instanceof DeclaredType)) {
            return false;
        }
        final Types types = env.getTypeUtils();
        genericType = types.erasure(genericType);
        if (!(types.isAssignable(type, genericType))) {
            return false;
        }
        final List<? extends TypeMirror> args = ((DeclaredType) type).getTypeArguments();
        if (args.size() != 1) {
            return false;
        }
        return types
                .isAssignable(args.get(0), parameterType);
    }

    public boolean isStringList(TypeMirror type) {
        return isSingleParameterizedType(type, ofType(TypeConstants.FQDN_STRING), ofType(TypeConstants.FQDN_LIST));
    }

    public boolean isParcelableList(TypeMirror type) {
        return isSingleParameterizedType(type, ofType(TypeConstants.FQDN_PARCELABLE), ofType(TypeConstants.FQDN_LIST));
    }

    public boolean isSerializableList(TypeMirror type) {
        return isSingleParameterizedType(type, ofType(TypeConstants.FQDN_SERIALIZABLE), ofType(TypeConstants.FQDN_LIST));
    }

    /**
     * Activity test.
     *
     * @param type The type.
     * @return {@code true} if {@code type} inherts from {@code Activity}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isActivity(TypeMirror type) {
        return isAssignable(type, TypeConstants.FQDN_ACTIVITY);
    }

    /**
     * Fragment test.
     *
     * @param type The type.
     * @return {@code true} if {@code type} inherits from {@code Fragment}
     */
    public boolean isFragment(TypeMirror type) {
        return isAssignable(type, TypeConstants.FQDN_FRAGMENT)
                || isAssignable(type, TypeConstants.FQDN_SUPPORT_FRAGMENT);
    }

    public boolean isSerializable(TypeMirror type) {
        return isAssignable(type, TypeConstants.FQDN_SERIALIZABLE);
    }

    public boolean isParcelable(TypeMirror type) {
        return isAssignable(type, TypeConstants.FQDN_PARCELABLE);
    }

    public TypeMirror ofType(String targetFqdn) {
        TypeElement element = env.getElementUtils().getTypeElement(targetFqdn);
        return element == null ? null : element.asType();
    }

    /**
     * Test {@code type} is assignable to {@code targetFqdn}.
     *
     * @param type       The source type.
     * @param targetFqdn The target type FQDN.
     * @return {@code true} if assignable.
     */
    private boolean isAssignable(TypeMirror type, String targetFqdn) {
        Types types = env.getTypeUtils();
        return types.isAssignable(type, ofType(targetFqdn));
    }


    /**
     * Test {@code type} is the same type of {@code targetFqdn}.
     *
     * @param type       The source type.
     * @param targetFqdn The target type FQDN
     * @return {@code true} if is the same.
     */
    public boolean isSameType(TypeMirror type, String targetFqdn) {
        Types types = env.getTypeUtils();
        return types.isSameType(type, ofType(targetFqdn));
    }

    public boolean isSameType(TypeMirror type1, TypeMirror type2) {
        return env.getTypeUtils()
                .isSameType(type1, type2);
    }

    public void i(String tag, String message) {
        if (debug) {
            env.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, String.format(MESSAGE_FORMAT, tag, message));
        }
    }

    public void e(String tag, String message) {
        env.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, String.format(MESSAGE_FORMAT, tag, message));
    }
}
