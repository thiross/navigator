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

@SuppressWarnings({"unused", "unchecked"})
public class Navigator {

    private static final Map<Class<?>, Constructor<? extends BundleBuilder>> BUILDERS = new LinkedHashMap<>();

    private static final Map<String, Constructor<? extends BundleBuilder>> FINDERS = new LinkedHashMap<>();

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
        Constructor<? extends BundleBuilder> constructor = findBuilderConstructorForClass(targetClass);
        if (constructor == null) {
            throw new RuntimeException("can't find builder class for " + targetClass.getSimpleName());
        }
        try {
            BundleBuilder builder = constructor.newInstance(bundle);
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
    private static Constructor<? extends BundleBuilder> findBuilderConstructorForClass(Class<?> cls) {
        Constructor<? extends BundleBuilder> constructor = BUILDERS.get(cls);
        if (constructor != null) {
            return constructor;
        }
        try {
            String clsName = cls.getName();
            Class<?> builderClass = cls.getClassLoader().loadClass(clsName + Constants.BUNDLE_BUILDER_CLASS_SUFFIX);
            constructor = (Constructor<? extends BundleBuilder>) builderClass.getConstructor(Bundle.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("can't find builder class for " + cls.getSimpleName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("can't find builder class for " + cls.getSimpleName());
        }
        BUILDERS.put(cls, constructor);
        return constructor;
    }

    public static BundleBuilder parse(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new RuntimeException("path must start with `/`");
        }
        int index = path.indexOf('/', 1);
        String page;
        if (index >= 0) {
            page = path.substring(1, index);
        } else {
            page = path.substring(1);
        }
        synchronized (FINDERS) {
            if (!finderInitialized) {
                initializeFinders();
            }
            Constructor<? extends BundleBuilder> constructor = FINDERS.get(page);
            try {
                if (constructor == null) {
                    return null;
                }
                BundleBuilder builder = constructor.newInstance();
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
