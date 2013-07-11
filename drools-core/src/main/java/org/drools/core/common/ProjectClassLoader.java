package org.drools.core.common;

import org.kie.internal.utils.ClassLoaderUtil;
import org.kie.internal.utils.CompositeClassLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.core.rule.JavaDialectRuntimeData.convertClassToResourcePath;

public class ProjectClassLoader extends ClassLoader {

    private static final boolean CACHE_NON_EXISTING_CLASSES = true;
    private static final ClassNotFoundException dummyCFNE = CACHE_NON_EXISTING_CLASSES ?
                                                            new ClassNotFoundException("This is just a cached Exception. Disable non existing classes cache to see the actual one.") :
                                                            null;

    private Map<String, byte[]> store;

    private final Set<String> nonExistingClasses = new HashSet<String>();

    private ClassLoader droolsClassLoader;

    private ProjectClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static ClassLoader getClassLoader(final ClassLoader[] classLoaders,
                                             final Class< ? > cls,
                                             final boolean enableCache) {
        if (classLoaders == null || classLoaders.length == 0) {
            return cls == null ? createProjectClassLoader() : createProjectClassLoader(cls.getClassLoader());
        } else if (classLoaders.length == 1) {
            ProjectClassLoader classLoader = createProjectClassLoader(classLoaders[0]);
            if (cls != null) {
                classLoader.setDroolsClassLoader(cls.getClassLoader());
            }
            return classLoader;
        } else {
            return ClassLoaderUtil.getClassLoader(classLoaders, cls, enableCache);
        }
    }

    public static ProjectClassLoader createProjectClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        if (parent == null) {
            parent = ProjectClassLoader.class.getClassLoader();
        }
        return new ProjectClassLoader(parent);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent) {
        if (parent == null) {
            return createProjectClassLoader();
        }
        return parent instanceof ProjectClassLoader ? (ProjectClassLoader)parent : new ProjectClassLoader(parent);
    }

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent, Map<String, byte[]> store) {
        ProjectClassLoader projectClassLoader = createProjectClassLoader(parent);
        projectClassLoader.store = store;
        return projectClassLoader;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
        } catch (ClassNotFoundException e1) {
            try {
                return Class.forName(name, resolve, getParent());
            } catch (ClassNotFoundException e2) {
                byte[] bytecode = getBytecode(convertClassToResourcePath(name));
                if (bytecode == null) {
                    if (CACHE_NON_EXISTING_CLASSES) {
                        nonExistingClasses.add(name);
                    }
                    throw e2;
                }
                return defineClass(name, bytecode, 0, bytecode.length);
            }
        }
    }

    public Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, convertClassToResourcePath(name), bytecode);
    }

    public Class<?> defineClass(String name, String resourceName, byte[] bytecode) {
        storeClass(name, resourceName, bytecode);
        return defineClass(name, bytecode, 0, bytecode.length);
    }

    public void storeClass(String name, String resourceName, byte[] bytecode) {
        int lastDot = name.lastIndexOf( '.' );
        if (lastDot > 0) {
            String pkgName = name.substring( 0, lastDot );
            if (getPackage( pkgName ) == null) {
                definePackage( pkgName, "", "", "", "", "", "", null );
            }
        }
        if (store == null) {
            store = new HashMap<String, byte[]>();
        }
        store.put(resourceName, bytecode);
        if (CACHE_NON_EXISTING_CLASSES) {
            nonExistingClasses.remove(name);
        }
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
}
