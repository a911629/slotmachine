package com.example.slotgame;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Field getField(Class<?> thisClass, String fieldName) {
        if (thisClass == null) {
            return null;
        }

        try {
            return thisClass.getDeclaredField(fieldName);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object getValue(Object instance, String fieldName) {
        Field field = getField(instance.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        // set access to true
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object getValue(Class clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (field == null) {
            return null;
        }
        // set access to true
        field.setAccessible(true);
        try {
            return field.get(null);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> thisClass, String methodName, Class<?>[] parameterTypes) {
        if (thisClass == null) {
            return null;
        }

        try {
            Method method = thisClass.getDeclaredMethod(methodName, parameterTypes);
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method;
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object invokeMethod(Object instance, String methodName, Object... args) throws Throwable {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }
        Method method = getMethod(instance.getClass(), methodName, parameterTypes);
        return method.invoke(instance, args);
    }

    public static Object invokeMethod(Class clazz, String methodName, Object... args) throws Throwable {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }
        Method method = getMethod(clazz, methodName, parameterTypes);
        return method.invoke(clazz, args);
    }
}
