package org.drools.ruleunits.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.DataSource;

public interface AssignableChecker {

    boolean isAssignableFrom(Class<?> source, Class<?> target);

    static AssignableChecker create(ClassLoader classLoader) {
        return create(classLoader, classLoader != DataSource.class.getClassLoader());
    }

    static AssignableChecker create(ClassLoader classLoader, boolean classLoaderSafe) {
        return classLoaderSafe ? new ClassLoaderSafeAssignableChecker(classLoader) : DummyAssignableChecker.INSTANCE;
    }

    enum DummyAssignableChecker implements AssignableChecker {

        INSTANCE;

        @Override
        public boolean isAssignableFrom(Class<?> source, Class<?> target) {
            return source.isAssignableFrom(target);
        }
    }

    class ClassLoaderSafeAssignableChecker implements AssignableChecker {
        private final ClassLoader classLoader;
        private final Map<String, Class<?>> classes = new HashMap<>();

        public ClassLoaderSafeAssignableChecker(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public boolean isAssignableFrom(Class<?> source, Class<?> target) {
            return classes.computeIfAbsent(source.getCanonicalName(), name -> {
                try {
                    return classLoader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    return source;
                }
            }).isAssignableFrom(target);
        }
    }
}
