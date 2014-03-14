package org.drools.core.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static org.drools.core.util.ClassUtils.convertClassToResourcePath;

public class ProjectClassLoader extends ClassLoader {

    private static final boolean CACHE_NON_EXISTING_CLASSES = true;
    private static final ClassNotFoundException dummyCFNE = CACHE_NON_EXISTING_CLASSES ?
                                                            new ClassNotFoundException("This is just a cached Exception. Disable non existing classes cache to see the actual one.") :
                                                            null;

    private static boolean isIBM_JVM = System.getProperty("java.vendor").toLowerCase().contains("ibm");

    private Map<String, byte[]> store;

    private Map<String, ClassBytecode> definedTypes;

    private final Set<String> nonExistingClasses = new HashSet<String>();

    private ClassLoader droolsClassLoader;

    private InternalTypesClassLoader typesClassLoader;

    private Set<String> loadedClasses = new HashSet<String>();

    private ProjectClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static class IBMClassLoader extends ProjectClassLoader {
        private final boolean parentImplemntsFindReosources;

        private static final Enumeration<URL> EMPTY_RESOURCE_ENUM = new Vector<URL>().elements();

        private IBMClassLoader(ClassLoader parent) {
            super(parent);
            Method m = null;
            try {
                m = parent.getClass().getMethod("findResources", String.class);
            } catch (NoSuchMethodException e) { }
            parentImplemntsFindReosources = m != null && m.getDeclaringClass() == parent.getClass();
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            // if the parent doesn't implemnt this method call getResources directly on it
            // see https://blogs.oracle.com/bhaktimehta/entry/ibm_jdk_and_classloader_getresources
            return parentImplemntsFindReosources ? EMPTY_RESOURCE_ENUM : getParent().getResources(name);
        }
    }

    private static ProjectClassLoader internalCreate(ClassLoader parent) {
        return isIBM_JVM ? new IBMClassLoader(parent) : new ProjectClassLoader(parent);
    }

    public static ClassLoader getClassLoader(final ClassLoader classLoader,
                                             final Class< ? > cls,
                                             final boolean enableCache) {
        ProjectClassLoader projectClassLoader = createProjectClassLoader(classLoader);
        if (cls != null) {
            projectClassLoader.setDroolsClassLoader(cls.getClassLoader());
        }
        return projectClassLoader;
    }

    public static ProjectClassLoader createProjectClassLoader() {
        return internalCreate(findParentClassLoader());
    }

    public static ClassLoader findParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        if (parent == null) {
            parent = ProjectClassLoader.class.getClassLoader();
        }
        return parent;
    }


    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent) {
        if (parent == null) {
            return createProjectClassLoader();
        }
        return parent instanceof ProjectClassLoader ? (ProjectClassLoader)parent : internalCreate(parent);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent, Map<String, byte[]> store) {
        ProjectClassLoader projectClassLoader = createProjectClassLoader(parent);
        projectClassLoader.store = store;
        return projectClassLoader;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        loadedClasses.add(name);
        try {
            return internalLoadClass(name, resolve);
        } catch (ClassNotFoundException e2) {
            return loadType(name, resolve);
        }
    }

    private Class<?> internalLoadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (CACHE_NON_EXISTING_CLASSES && nonExistingClasses.contains(name)) {
            throw dummyCFNE;
        }

        if (droolsClassLoader != null) {
            try {
                return Class.forName(name, resolve, droolsClassLoader);
            } catch (ClassNotFoundException e) { }
        }
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            return Class.forName(name, resolve, getParent());
        }
    }

    private Class<?> loadType(String name, boolean resolve) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;
        if (typesClassLoader != null) {
            try {
                return typesClassLoader.loadType(name, resolve);
            } catch (ClassNotFoundException e) {
                cnfe = e;
            }
        }
        return tryDefineType(name, cnfe);
    }

    private Class<?> tryDefineType(String name, ClassNotFoundException cnfe) throws ClassNotFoundException {
        byte[] bytecode = getBytecode(convertClassToResourcePath(name));
        if (bytecode == null) {
            if (CACHE_NON_EXISTING_CLASSES) {
                nonExistingClasses.add(name);
            }
            throw cnfe != null ? cnfe : new ClassNotFoundException(name);
        }
        return defineType(name, bytecode);
    }

    private Class<?> defineType(String name, byte[] bytecode) {
        if (definedTypes == null) {
            definedTypes = new HashMap<String, ClassBytecode>();
        } else {
            ClassBytecode existingClass = definedTypes.get(name);
            if (existingClass != null && Arrays.equals(bytecode, existingClass.bytes)) {
                return existingClass.clazz;
            }
        }

        if (typesClassLoader == null) {
            typesClassLoader = new InternalTypesClassLoader(this);
        }
        Class<?> clazz = typesClassLoader.defineClass(name, bytecode);
        definedTypes.put(name, new ClassBytecode(clazz, bytecode));
        return clazz;
    }

    public Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, convertClassToResourcePath(name), bytecode);
    }

    public Class<?> defineClass(String name, String resourceName, byte[] bytecode) {
        storeClass(name, resourceName, bytecode);
        return defineType(name, bytecode);
    }

    public void undefineClass(String name) {
        String resourceName = convertClassToResourcePath(name);
        if (store.remove(resourceName) != null) {
            if (CACHE_NON_EXISTING_CLASSES) {
                nonExistingClasses.add(name);
            }
            typesClassLoader = null;
        }
    }

    public void storeClass(String name, byte[] bytecode) {
        storeClass(name, convertClassToResourcePath(name), bytecode);
    }

    public void storeClass(String name, String resourceName, byte[] bytecode) {
        if (store == null) {
            store = new HashMap<String, byte[]>();
        }
        store.put(resourceName, bytecode);
        if (CACHE_NON_EXISTING_CLASSES) {
            nonExistingClasses.remove(name);
        }
    }

    public boolean isClassInUse(String className) {
        return loadedClasses.contains(className);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] bytecode = getBytecode(name);
        return bytecode != null ? new ByteArrayInputStream( bytecode ) : super.getResourceAsStream(name);
    }

    @Override
    public URL getResource(String name) {
        if (droolsClassLoader != null) {
            URL resource = droolsClassLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return super.getResource(name);
    }

    public byte[] getBytecode(String resourceName) {
        return store == null ? null : store.get(resourceName);
    }

    public Map<String, byte[]> getStore() {
        return store;
    }

    public void setDroolsClassLoader(ClassLoader droolsClassLoader) {
        if (getParent() != droolsClassLoader) {
            this.droolsClassLoader = droolsClassLoader;
            if (CACHE_NON_EXISTING_CLASSES) {
                nonExistingClasses.clear();
            }
        }
    }

    public void initFrom(ProjectClassLoader other) {
        if (other.store != null) {
            if (store == null) {
                store = new HashMap<String, byte[]>();
            }
            store.putAll(other.store);
        }
        nonExistingClasses.addAll(other.nonExistingClasses);
    }

    private static class InternalTypesClassLoader extends ClassLoader {

        private final ProjectClassLoader projectClassLoader;

        private InternalTypesClassLoader(ProjectClassLoader projectClassLoader) {
            this.projectClassLoader = projectClassLoader;
        }

        public Class<?> defineClass(String name, byte[] bytecode) {
            int lastDot = name.lastIndexOf( '.' );
            if (lastDot > 0) {
                String pkgName = name.substring( 0, lastDot );
                if (getPackage( pkgName ) == null) {
                    definePackage( pkgName, "", "", "", "", "", "", null );
                }
            }
            return defineClass(name, bytecode, 0, bytecode.length);
        }

        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                return loadType(name, resolve);
            } catch (ClassNotFoundException cnfe) {
                synchronized(projectClassLoader) {
                    try {
                        return projectClassLoader.internalLoadClass(name, resolve);
                    } catch (ClassNotFoundException cnfe2) {
                        return projectClassLoader.tryDefineType(name, cnfe);
                    }
                }
            }
        }

        private Class<?> loadType(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }

    public synchronized void reinitTypes() {
        typesClassLoader = null;
        nonExistingClasses.clear();
    }

    private static class ClassBytecode {
        private final Class<?> clazz;
        private final byte[] bytes;

        private ClassBytecode(Class<?> clazz, byte[] bytes) {
            this.clazz = clazz;
            this.bytes = bytes;
        }
    }
}
