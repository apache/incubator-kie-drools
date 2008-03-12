package org.drools.rule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class MapBackedClassLoader extends ClassLoader
    implements
    DroolsClassLoader,
    Serializable {

    private static final long             serialVersionUID = 400L;

    private static final ProtectionDomain PROTECTION_DOMAIN;

    private Map                           store;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {
            public Object run() {
                return MapBackedClassLoader.class.getProtectionDomain();
            }
        } );
    }

    public MapBackedClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        this.store = new HashMap();
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
            byte[] clazzBytes = null;
            synchronized ( this.store ) {            
                clazzBytes = (byte[]) this.store.get( name );
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
        byte[] bytes = null;
        synchronized ( this.store ) {            
            bytes = (byte[]) this.store.get( name );
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
