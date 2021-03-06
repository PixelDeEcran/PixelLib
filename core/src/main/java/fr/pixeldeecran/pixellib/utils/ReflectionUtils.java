package fr.pixeldeecran.pixellib.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static Set<Class<?>> findAllClassesIn(String packageName) {
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replace('.', '/'));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(line -> line.substring(0, line.length() - 6))
            .map(line -> {
                try {
                    return Class.forName(packageName + '.' + line);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            })
            .collect(Collectors.toSet());
    }

    public static Class<?> getClass(String name, boolean initialize, ClassLoader classLoader) {
        try {
            return Class.forName(name, initialize, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MethodHandle getPrivateMethod(Class<?> clazz, String name, Class<?>... parameters) {
        try {
            Method method = clazz.getDeclaredMethod(name, parameters);
            method.setAccessible(true);
            return MethodHandles.lookup().unreflect(method);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MethodHandle getFieldSetter(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectSetter(field);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MethodHandle getFieldGetter(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectGetter(field);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(MethodHandle methodHandle, Object... parameters) {
        try {
            return methodHandle.invokeWithArguments(Arrays.asList(parameters));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(Class<?> clazz, String name, Object instance, Object... parameters) {
        try {
            Method method = clazz.getDeclaredMethod(name);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(instance, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValue(Class<?> clazz, String name, Object instance) {
        try {
            Field field = clazz.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
