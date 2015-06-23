/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import org.drools.core.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<String, Class<?>>();

    private final ResourceProvider resourceProvider;

    private ProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        super(parent);
        this.resourceProvider = resourceProvider;
    }

    public static class IBMClassLoader extends ProjectClassLoader {
        private final boolean parentImplemntsFindReosources;

        private static final Enumeration<URL> EMPTY_RESOURCE_ENUM = new Vector<URL>().elements();

        private IBMClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
            super(parent, resourceProvider);
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

    private static ProjectClassLoader internalCreate(ClassLoader parent, ResourceProvider resourceProvider) {
        return isIBM_JVM ? new IBMClassLoader(parent, resourceProvider) : new ProjectClassLoader(parent, resourceProvider);
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


    public static ProjectClassLoader createProjectClassLoader() {
        return internalCreate(findParentClassLoader(), null);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent) {
        return createProjectClassLoader(parent, (ResourceProvider)null);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        if (parent == null) {
            return internalCreate(findParentClassLoader(), resourceProvider);
        }
        return parent instanceof ProjectClassLoader ? (ProjectClassLoader)parent : internalCreate(parent, resourceProvider);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent, Map<String, byte[]> store) {
        ProjectClassLoader projectClassLoader = createProjectClassLoader(parent);
        projectClassLoader.store = store;
        return projectClassLoader;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = loadedClasses.get(name);
        if (cls != null) {
            return cls;
        }
        synchronized (this) {
            try {
                cls = internalLoadClass(name, resolve);
            } catch (ClassNotFoundException e2) {
                cls = loadType(name, resolve);
            }
        }
        loadedClasses.put(name, cls);
        return cls;
    }

    Class<?> internalLoadClass(String name, boolean resolve) throws ClassNotFoundException {
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

    Class<?> tryDefineType(String name, ClassNotFoundException cnfe) throws ClassNotFoundException {
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
            typesClassLoader = makeClassLoader();
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
        return loadedClasses.containsKey(className);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] bytecode = getBytecode(name);
        if (bytecode != null) {
            return new ByteArrayInputStream( bytecode );
        }
        if (resourceProvider != null) {
            try {
                InputStream is = resourceProvider.getResourceAsStream(name);
                if (is != null) {
                    return is;
                }
            } catch (IOException e) { }
        }
        return super.getResourceAsStream(name);
    }

    @Override
    public URL getResource(String name) {
        if (droolsClassLoader != null) {
            URL resource = droolsClassLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        if (resourceProvider != null) {
            URL resource = resourceProvider.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> resources = super.getResources(name);
        if (resourceProvider != null) {
            URL providedResource = resourceProvider.getResource(name);
            if (resources != null) {
                return new ResourcesEnum(providedResource, resources);
            }
        }
        return resources;
    }

    private static class ResourcesEnum implements Enumeration<URL> {

        private URL providedResource;
        private final Enumeration<URL> resources;

        private ResourcesEnum(URL providedResource, Enumeration<URL> resources) {
            this.providedResource = providedResource;
            this.resources = resources;
        }

        @Override
        public boolean hasMoreElements() {
            return providedResource != null || resources.hasMoreElements();
        }

        @Override
        public URL nextElement() {
            if (providedResource != null) {
                URL result = providedResource;
                providedResource = null;
                return result;
            }
            return resources.nextElement();
        }
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

    private InternalTypesClassLoader makeClassLoader() {
        return ClassUtils.isAndroid() ?
                (InternalTypesClassLoader) ClassUtils.instantiateObject(
                        "org.drools.core.common.DexInternalTypesClassLoader", null, this) :
                new DefaultInternalTypesClassLoader( this );
    }

    interface InternalTypesClassLoader {
        Class<?> defineClass(String name, byte[] bytecode);
        Class<?> loadType(String name, boolean resolve) throws ClassNotFoundException;
    }

    private static class DefaultInternalTypesClassLoader extends ClassLoader implements InternalTypesClassLoader {

        private final ProjectClassLoader projectClassLoader;

        private DefaultInternalTypesClassLoader(ProjectClassLoader projectClassLoader) {
            super(projectClassLoader.getParent());
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

        public Class<?> loadType(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }

    public synchronized void reinitTypes() {
        typesClassLoader = null;
        nonExistingClasses.clear();
        loadedClasses.clear();
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
