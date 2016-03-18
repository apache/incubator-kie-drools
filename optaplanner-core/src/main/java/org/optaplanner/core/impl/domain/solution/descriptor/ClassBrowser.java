package org.optaplanner.core.impl.domain.solution.descriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassBrowser {

    private static Set<Class<?>> getAllClassesInHierarchy(Class<?> root) {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(root);
        Class<?> superClass = root.getSuperclass();
        while (superClass != Object.class) {
            classes.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return classes;
    }

    /**
     *
     * @param root Never null, the class in which to start the search.
     * @return Never null; all fields in the class, and all non-private non-static fields in super-types.
     */
    public static Set<Field> getAllVisibleFields(Class<?> root) {
        Set<Class<?>> classes = getAllClassesInHierarchy(root);
        Set<Field> visibleFields = new HashSet<>();
        classes.forEach(c -> Arrays.stream(c.getDeclaredFields()).forEach(f -> {
            int mods = f.getModifiers();
            if (!Modifier.isStatic(mods) && (c == root || !Modifier.isPrivate(mods))) {
                visibleFields.add(f);
            }
        }));
        return visibleFields;
    }

    /**
     *
     * @param root Never null, the class in which to start the search.
     * @return Never null; all methods in the class, and all non-private non-abstract non-static fields in super-types.
     */
    public static Set<Method> getAllVisibleMethods(Class<?> root) {
        Set<Class<?>> classes = getAllClassesInHierarchy(root);
        Set<Method> visibleMethods = new HashSet<>();
        classes.forEach(c -> Arrays.stream(c.getDeclaredMethods()).forEach(m -> {
            int mods = m.getModifiers();
            if (!Modifier.isStatic(mods) && !Modifier.isAbstract(mods) && (c == root || !Modifier.isPrivate(mods))) {
                visibleMethods.add(m);
            }
        }));
        return visibleMethods;
    }

}
