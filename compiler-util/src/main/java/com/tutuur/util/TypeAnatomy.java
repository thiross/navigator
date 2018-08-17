package com.tutuur.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Java类型判断工具类
 *
 * @author yujie
 */
public class TypeAnatomy {

    private final Elements elements;

    private final Types types;

    /**
     * 从ProcessingEnvironment获取相关参数，构造实例
     *
     * @param env AnnotationProcessor对应的环境
     */
    public TypeAnatomy(ProcessingEnvironment env) {
        elements = env.getElementUtils();
        types = env.getTypeUtils();
    }

    /**
     * 判断是否是Activity
     *
     * @param element 待判断的类型
     * @return 返回{@code} 如果{@code element}继承自{@code Activity}
     */
    public boolean isActivity(TypeElement element) {
        return isAssignable(element.asType(), TypeConstants.FQDN_ACTIVITY);
    }

    /**
     * 判断是否是Fragment
     *
     * @param element 待判断的类型
     * @return 返回{@code} 如果{@code element}继承自{@code Fragment}
     */
    public boolean isFragment(TypeElement element) {
        return isAssignable(element.asType(), TypeConstants.FQDN_FRAGMENT)
                || isAssignable(element.asType(), TypeConstants.FQDN_SUPPORT_FRAGMENT);
    }

    /**
     * 判断{@code type}是否可赋值到{@code targetFqdn}类型
     *
     * @param type       待判断的类型
     * @param targetFqdn 目标类型FQDN字符串
     * @return 返回{@code true}如果可以赋值
     */
    private boolean isAssignable(TypeMirror type, String targetFqdn) {
        return types.isAssignable(type, elements.getTypeElement(targetFqdn) == null ?
                null : elements.getTypeElement(targetFqdn).asType());
    }


    /**
     * 判断{@code type}是否完全匹配{@code targetFqdn}类型
     *
     * @param type       待判断的类型
     * @param targetFqdn 目标类型FQDN字符串
     * @return 返回{@code true}如果完全匹配
     */
    private boolean isSameType(TypeMirror type, String targetFqdn) {
        return types.isSameType(type, elements.getTypeElement(targetFqdn) == null ?
                null : elements.getTypeElement(targetFqdn).asType());
    }
}
