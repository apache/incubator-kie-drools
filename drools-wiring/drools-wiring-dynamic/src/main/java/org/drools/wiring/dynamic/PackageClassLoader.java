/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.wiring.dynamic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.kie.internal.utils.FastClassLoader;
import org.kie.memorycompiler.WritableClassLoader;

public class PackageClassLoader extends ClassLoader implements FastClassLoader, WritableClassLoader {

    private static final ProtectionDomain PROTECTION_DOMAIN;

    private final ConcurrentHashMap<String, Object> parallelLockMap = new ConcurrentHashMap<>();

    protected Map<String, byte[]> store;

    private Set<String> existingPackages = new ConcurrentSkipListSet<>();

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged((PrivilegedAction) () -> PackageClassLoader.class.getProtectionDomain());
    }

    public PackageClassLoader(Map<String, byte[]> store, ClassLoader rootClassLoader) {
        super( rootClassLoader );
        this.store = store;
    }

    public Class<?> loadClass( final String name,
                               final boolean resolve ) throws ClassNotFoundException {
        Class<?> cls = fastFindClass( name );

        if (cls == null) {
            ClassLoader parent = getParent();
            cls = parent.loadClass( name );
        }

        if (cls == null) {
            throw new ClassNotFoundException( "Unable to load class: " + name );
        }

        return cls;
    }

    public Class<?> fastFindClass( final String name ) {
        Class<?> cls = findLoadedClass( name );

        if (cls == null) {
            Object lock = getLockObject(name);
            synchronized (lock) {
                cls = findLoadedClass( name );
                if (cls == null) {
                    try {
                        cls = internalDefineClass( name, this.store.get( convertClassToResourcePath( name ) ) );
                    } finally {
                        releaseLockObject( name );
                    }
                }
            }
        }

        return cls;
    }

    private Class<?> internalDefineClass( String name, byte[] clazzBytes ) {
        if ( clazzBytes == null ) {
            return null;
        }
        String pkgName = name.substring( 0,
                name.lastIndexOf( '.' ) );

        if ( !existingPackages.contains( pkgName ) ) {
            synchronized (this) {
                if ( getPackage( pkgName ) == null ) {
                    definePackage( pkgName,
                            "", "", "", "", "", "",
                            null );
                }
                existingPackages.add( pkgName );
            }
        }

        Class<?> cls = writeClass( name, clazzBytes );
        resolveClass( cls );
        return cls;
    }

    public InputStream getResourceAsStream(final String name ) {
        final byte[] clsBytes = this.store.get( name );
        return clsBytes != null ? new ByteArrayInputStream( clsBytes ) : getParent().getResourceAsStream( name );
    }

    public URL getResource(String name ) {
        return getParent().getResource( name );
    }

    public Enumeration<URL> getResources(String name ) throws IOException {
        return getParent().getResources( name );
    }

    private Object getLockObject(String className) {
        Object newLock = new Object();
        Object lock = parallelLockMap.putIfAbsent(className, newLock);
        return lock != null ? lock : newLock;
    }

    private void releaseLockObject(String className) {
        parallelLockMap.remove( className );
    }

    @Override
    public Class<?> writeClass( String name, byte[] bytecode ) {
        return defineClass( name, bytecode, 0, bytecode.length, PROTECTION_DOMAIN );
    }

    private static String convertClassToResourcePath(final String pName) {
        return pName.replace( '.', '/' ) + ".class";
    }
}