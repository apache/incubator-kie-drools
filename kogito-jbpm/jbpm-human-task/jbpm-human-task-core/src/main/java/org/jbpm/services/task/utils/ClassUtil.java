package org.jbpm.services.task.utils;

public final class ClassUtil {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>)aClass;
    }
}
