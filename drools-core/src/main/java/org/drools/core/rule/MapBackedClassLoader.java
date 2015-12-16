/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.rule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;


public class MapBackedClassLoader extends ClassLoader {

    private static final long             serialVersionUID = 510l;

    private static final ProtectionDomain PROTECTION_DOMAIN;

    private Map<String, byte[]>           store;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {
            public Object run() {
                return MapBackedClassLoader.class.getProtectionDomain();
            }
        } );
    }

    public MapBackedClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        this.store = new HashMap<String, byte[]>();
    }

    public MapBackedClassLoader(final ClassLoader parentClassLoader,
                                final Map store) {
        super( parentClassLoader );
        this.store = store;
    }

    public void addResource(String className,
                            byte[] bytes) {
        addClass( className,
                  bytes );
    }

    private String convertResourcePathToClassName(final String pName) {
        return pName.replaceAll( ".java$|.class$",
                                 "" ).replace( '/',
                                               '.' );
    }

    public void addClass(final String className,
                         byte[] bytes) {
        synchronized ( this.store ) {
            this.store.put( convertResourcePathToClassName( className ),
                            bytes );
        }
    }

    public Class fastFindClass(final String name) {
        final Class clazz = findLoadedClass( name );

        if ( clazz == null ) {
            byte[] clazzBytes;
            synchronized ( this.store ) {
                clazzBytes = this.store.get( name );
            }

            if ( clazzBytes != null ) {
                return defineClass( name,
                                    clazzBytes,
                                    0,
                                    clazzBytes.length,
                                    PROTECTION_DOMAIN );
            }
        }
        return clazz;
    }

    /**
     * Javadocs recommend that this method not be overloaded. We overload this so that we can prioritise the fastFindClass
     * over method calls to parent.loadClass(name, false); and c = findBootstrapClass0(name); which the default implementation
     * would first - hence why we call it "fastFindClass" instead of standard findClass, this indicates that we give it a
     * higher priority than normal.
     */
    public synchronized Class loadClass(final String name,
                                        final boolean resolve) throws ClassNotFoundException {
        Class clazz = fastFindClass( name );

        if ( clazz == null ) {
            final ClassLoader parent = getParent();
            if ( parent != null ) {
                clazz = Class.forName( name,
                                       true,
                                       parent );
            }
        }

        if ( resolve ) {
            resolveClass( clazz );
        }

        return clazz;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        return fastFindClass( name );
    }

    public InputStream getResourceAsStream(final String name) {
        byte[] bytes = null;
        synchronized ( this.store ) {
            bytes = this.store.get( convertResourcePathToClassName( name ) );
        }

        if ( bytes != null ) {
            return new ByteArrayInputStream( bytes );
        } else {
            InputStream input = this.getParent().getResourceAsStream( name );
            if ( input == null ) {
                input = super.getResourceAsStream( name );
            }
            return input;
        }
    }

    public Map getStore() {
        return this.store;
    }
}
