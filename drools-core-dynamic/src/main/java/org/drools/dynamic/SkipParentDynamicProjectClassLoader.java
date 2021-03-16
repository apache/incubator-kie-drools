package org.drools.dynamic;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import org.drools.reflective.ResourceProvider;

public class SkipParentDynamicProjectClassLoader extends DynamicProjectClassLoader {

    private Set<String> generatedClassNames = new HashSet<>();

    protected SkipParentDynamicProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        super(parent, resourceProvider);
    }

    @Override
    public Set<String> getGeneratedClassNames() {
        return generatedClassNames;
    }

    @Override
    public void setGeneratedClassNames(Set<String> generatedClassNames) {
        this.generatedClassNames = generatedClassNames;
    }

    @Override
    public InternalTypesClassLoader makeClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<InternalTypesClassLoader>) () -> new SkipParentInternalTypesClassLoader(this));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = loadedClasses.get(name);
        if (cls != null) {
            return cls;
        }

        if (generatedClassNames.contains(name)) {
            Class<?> clazz = findLoadedClass(name); // skip parent classloader
            if (clazz != null) {
                return clazz;
            }
            if (typesClassLoader != null) {
                clazz = ((SkipParentInternalTypesClassLoader)typesClassLoader).findLoadedClassWithoutParent(name);
                if (clazz != null) {
                    return clazz;
                }
            }
            // if generated class, go straight to defineType
            cls = tryDefineType(name, null);
        } else {
            try {
                cls = internalLoadClass(name, resolve);
            } catch (ClassNotFoundException e2) {
                // for stored classes which are not in generatedClassNames
                cls = loadType(name, resolve);
            }
        }

        loadedClasses.put(name, cls);
        return cls;
    }

    public static class SkipParentInternalTypesClassLoader extends DefaultInternalTypesClassLoader {

        static {
            registerAsParallelCapable();
        }

        private SkipParentInternalTypesClassLoader(SkipParentDynamicProjectClassLoader projectClassLoader) {
            super(projectClassLoader);
        }

        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (((SkipParentDynamicProjectClassLoader)projectClassLoader).getGeneratedClassNames().contains(name)) {
                Class<?> clazz = findLoadedClass(name); // skip parent classloader
                if (clazz != null) {
                    return clazz;
                }
                // if generated class, go straight to defineType
                return projectClassLoader.tryDefineType(name, null);
            }
            return super.loadClass(name, resolve);
        }

        public Class<?> findLoadedClassWithoutParent( String name ) {
            return findLoadedClass(name);
        }
    }
}
