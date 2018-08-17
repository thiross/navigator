package com.tutuur.navigator;

import com.google.auto.service.AutoService;
import com.tutuur.util.AnnotationProcessorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

@SuppressWarnings("unused")
@AutoService(Processor.class)
public class NavigationProcessor extends AbstractProcessor {

    private static final String TAG = NavigationProcessor.class.getSimpleName();

    private static final boolean DEBUG = true;

    private AnnotationProcessorHelper helper;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>(7);
        types.add(Navigation.class.getCanonicalName());
        types.add(BundleExtra.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        helper = new AnnotationProcessorHelper(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, List<VariableElement>> navigation = buildNavigation(env);
        if (navigation.isEmpty()) {
            helper.i(TAG, "No navigation class found.");
            return false;
        }
        Set<TypeElement> clazzSet = navigation.keySet();
        String packageName = findPackageName(clazzSet);
        if (DEBUG) {
            helper.i(TAG, String.format("Find common package name: %s", packageName));
        }
        for (TypeElement clazz : clazzSet) {
            if (DEBUG) {
                helper.i(TAG, String.format("Generating navigation for %s", clazz.getQualifiedName()));
            }
        }
        return false;
    }

    private Map<TypeElement, List<VariableElement>> buildNavigation(RoundEnvironment env) {
        Map<TypeElement, List<VariableElement>> navigation = new HashMap<>();
        for (Element element : env.getElementsAnnotatedWith(BundleExtra.class)) {
            final TypeElement clazz = (TypeElement) element.getEnclosingElement();
            if (!helper.isActivity(clazz)) {
                helper.e(TAG, String.format("%s is annotated with @BundleExtra but %s is not a Activity", element.getSimpleName(), clazz.getQualifiedName()));
            }
            if (!navigation.containsKey(clazz)) {
                navigation.put(clazz, new ArrayList<VariableElement>());
            }
            navigation.get(clazz)
                    .add((VariableElement) element);
        }
        for (Element element : env.getElementsAnnotatedWith(Navigation.class)) {
            final TypeElement clazz = (TypeElement) element;
            if (!helper.isActivity(clazz)) {
                helper.e(TAG, String.format("@Navigation annotated %s is not a Activity", (clazz).getQualifiedName()));
            }
            if (!navigation.containsKey(clazz)) {
                navigation.put(clazz, new ArrayList<VariableElement>());
            }
        }
        return navigation;
    }

    private String findPackageName(Set<TypeElement> clazzSet) {
        String name = null;
        for (TypeElement clazz : clazzSet) {
            String clazzName = clazz.getQualifiedName().toString();
            int index = clazzName.lastIndexOf('.');
            if (index >= 0) {
                clazzName = clazzName.substring(0, index);
            } else {
                helper.e(TAG, "No common package name found for all annotated classes.");
            }
            if (name == null) {
                name = clazzName;
             } else {
                while (!clazzName.startsWith(name)) {
                    index = name.lastIndexOf('.');
                    if (index < 0) {
                        helper.e(TAG, "No common package name found for all annotated classes.");
                    }
                    name = name.substring(0, index);
                }
            }
        }
        return name;
    }

}
