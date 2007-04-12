/*
 * Copyright 2006 JBoss Inc
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

package org.drools.resource.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A class loader for in memory byte[] resources
 * 
 * @author etirelli
 */
public class ByteArrayClassLoader extends ClassLoader {

    private final Map resources = new HashMap();

    public ByteArrayClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
    }

    public void addResource(final String name,
                            final byte[] bytecode) {
        this.resources.put( name,
                            bytecode );
    }

    public Class fastFindClass(final String name) {
        final Class clazz = findLoadedClass( name );

        if ( clazz == null ) {
            final byte[] clazzBytes = (byte[]) this.resources.get( convertClassToResourcePath( name ) );
            if ( clazzBytes != null ) {
                return defineClass( name,
                                    clazzBytes,
                                    0,
                                    clazzBytes.length );
            }
        }

        return clazz;
    }

    /**
     * Javadocs recommend that this method not be overloaded. We overload this so that we can prioritise the fastFindClass 
     * over method calls to parent.loadClass(name, false); and c = findBootstrapClass0(name); which the default implementation
     * would first - hence why we call it "fastFindClass" instead of standard findClass, this indicates that we give it a 
     * higher priority than normal.
     * 
     */
    protected synchronized Class loadClass(final String name,
                                           final boolean resolve) throws ClassNotFoundException {
        Class clazz = fastFindClass( name );

        if ( clazz == null ) {
            final ClassLoader parent = getParent();
            if ( parent != null ) {
                clazz = parent.loadClass( name );
            } else {
                throw new ClassNotFoundException( name );
            }
        }

        if ( resolve ) {
            resolveClass( clazz );
        }

        return clazz;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class clazz = fastFindClass( name );
        if ( clazz == null ) {
            throw new ClassNotFoundException( name );
        }
        return clazz;
    }

    public InputStream getResourceAsStream(final String name) {
        final byte[] bytes = (byte[]) this.resources.get( name );
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

    //    /**
    //     * org/my/Class.xxx -> org.my.Class
    //     */
    //    private static String convertResourceToClassName( final String pResourceName ) {
    //        return stripExtension(pResourceName).replace('/', '.');
    //    }
    //
    /**
     * org.my.Class -> org/my/Class.class
     */
    private static String convertClassToResourcePath(final String pName) {
        return pName.replace( '.',
                              '/' ) + ".class";
    }

    //    /**
    //     * org/my/Class.xxx -> org/my/Class
    //     */
    //    private static String stripExtension( final String pResourceName ) {
    //        final int i = pResourceName.lastIndexOf('.');
    //        final String withoutExtension = pResourceName.substring(0, i);
    //        return withoutExtension;
    //    }

}
