package com.tutuur.navigator;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import com.tutuur.navigator.constants.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.tutuur.navigator.constants.Constants.PATTERN_INIT_CLASS;

@SuppressWarnings("unused")
public class Navigator {

    private static final Map<Class<?>, Constructor<? extends IntentBuilder>> BUILDERS = new LinkedHashMap<>();

    private static final Map<String, Constructor<? extends IntentBuilder>> FINDERS = new LinkedHashMap<>();

    private static boolean finderInitialized = false;

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

    @UiThread
    private static Constructor<? extends IntentBuilder> findBuilderConstructorForClass(Class<?> cls) {
        Constructor<? extends IntentBuilder> constructor = BUILDERS.get(cls);
        if (constructor != null) {
            return constructor;
        }
        try {
            String clsName = cls.getName();
            Class<?> builderClass = cls.getClassLoader().loadClass(clsName + Constants.INTENT_BUILDER_CLASS_SUFFIX);
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

    public static IntentBuilder parse(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new RuntimeException("path must start with `/`");
        }
        int index = path.indexOf('/', 1);
        String page = path;
        if (index >= 0) {
            page = path.substring(1, index);
        }
        synchronized (FINDERS) {
            if (!finderInitialized) {
                initializeFinders();
            }
            Constructor<? extends IntentBuilder> constructor = FINDERS.get(page);
            try {
                if (constructor == null) {
                    return null;
                }
                IntentBuilder builder = constructor.newInstance();
                builder.parse(path);
                return builder;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            }
        }
    }

    private static void initializeFinders() {
        try {
            Class<?> initClass = Navigator.class.getClassLoader()
                    .loadClass(PATTERN_INIT_CLASS);
            Method initMethod = initClass.getMethod("init", Map.class);
            initMethod.invoke(null, FINDERS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("init class " + PATTERN_INIT_CLASS + " not found.");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("init method of " + PATTERN_INIT_CLASS + " not found.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("init method of " + PATTERN_INIT_CLASS + " failure.");
        } catch (InvocationTargetException e) {
            Log.e("DEFG", "abc", e);
            throw new RuntimeException("init method of " + PATTERN_INIT_CLASS + " failure.");
        }
        finderInitialized = true;
    }
}
