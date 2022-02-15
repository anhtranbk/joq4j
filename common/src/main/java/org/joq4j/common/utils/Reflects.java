package org.joq4j.common.utils;

import org.joq4j.exceptions.ReflectionException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
public class Reflects {

    static final ClassLoader mainCl = Reflects.class.getClassLoader();

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassInstance(T object) {
        return (Class<T>) object.getClass();
    }

    public static <T> T newInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (IllegalAccessException | InstantiationException | NullPointerException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> T newInstance(String className) {
        try {
            return (T) mainCl.loadClass(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> T newInstance(String className, Class<?>[] parameterTypes, Object... parameters) {
        try {
            return (T) mainCl.loadClass(className).getConstructor(parameterTypes).newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }
}
