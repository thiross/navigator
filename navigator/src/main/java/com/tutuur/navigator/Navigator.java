package com.tutuur.navigator;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Navigator {

    private static final Map<Class<?>, Constructor<? extends IntentBuilder>> BUILDERS = new LinkedHashMap<>();

    @UiThread
    public static void bind(Activity target) {
        bind(target, target.getIntent().getExtras());
    }

    @UiThread
    public static void bind(Fragment target) {
        bind(target, target.getArguments());
    }

    @UiThread
    public static void bind(android.support.v4.app.Fragment target) {
        bind(target, target.getArguments());
    }

    @UiThread
    private static void bind(Object target, @Nullable Bundle bundle) {
        if (bundle == null) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Constructor<? extends IntentBuilder> constructor = findBuilderConstructorForClass(targetClass);
        if (constructor == null) {
            throw new RuntimeException("can't find builder class for " + targetClass.getSimpleName());
        }
        try {
            IntentBuilder builder = constructor.newInstance(bundle);
            builder.bind(target);
        } catch (InstantiationException e) {
            // do nothing.
        } catch (IllegalAccessException e) {
            // do nothing.
        } catch (InvocationTargetException e) {
            // do nothing.
        }
    }

    private static Constructor<? extends IntentBuilder> findBuilderConstructorForClass(Class<?> cls) {
        Constructor<? extends IntentBuilder> constructor = BUILDERS.get(cls);
        if (constructor != null) {
            return constructor;
        }
        try {
            String clsName = cls.getName();
            Class<?> builderClass = cls.getClassLoader().loadClass(clsName + "_IntentBuilder");
            //noinspection unchecked
            constructor = (Constructor<? extends IntentBuilder>) builderClass.getConstructor(Bundle.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("can't find builder class for " + cls.getSimpleName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("can't find builder class for " + cls.getSimpleName());
        }
        BUILDERS.put(cls, constructor);
        return constructor;
    }
}
