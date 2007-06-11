package org.drools.rule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.ObjectInputStreamWithLoader;

public class MapBackedClassLoader extends ClassLoader
    implements
    DroolsClassLoader,
    Serializable {
    private Map store;

    public MapBackedClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        this.store = new HashMap();
    }

    public void addClass(String className,
                         byte[] bytes) {
        this.store.put( className,
                        bytes );
    }

    public Class fastFindClass(final String name) {
        final Class clazz = findLoadedClass( name );

        if ( clazz == null ) {
            final byte[] clazzBytes = (byte[]) this.store.get( name );
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
        final byte[] bytes = (byte[]) this.store.get( name );
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

}
