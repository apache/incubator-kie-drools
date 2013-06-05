package org.drools.core.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.drools.core.rule.JavaDialectRuntimeData.convertClassToResourcePath;

public class ProjectClassLoader extends ClassLoader {

    private Map<String, byte[]> store;

    private ProjectClassLoader(ClassLoader parent) {
        super(parent);
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
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e1) {
            try {
                return Class.forName(name, resolve, getParent());
            } catch (ClassNotFoundException e2) {
                byte[] bytecode = getBytecode(convertClassToResourcePath(name));
                if (bytecode == null) {
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
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] bytecode = getBytecode(name);
        return bytecode != null ? new ByteArrayInputStream( bytecode ) : super.getResourceAsStream(name);
    }

    public byte[] getBytecode(String resourceName) {
        return store == null ? null : store.get(resourceName);
    }

    public Map<String, byte[]> getStore() {
        return store;
    }
}
