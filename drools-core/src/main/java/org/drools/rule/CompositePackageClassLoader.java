package org.drools.rule;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CompositePackageClassLoader extends ClassLoader
    implements
    DroolsClassLoader {

    private final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    private final List<ClassLoader> parents      = new ArrayList<ClassLoader>();

    public CompositePackageClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        this.parents.add( getParent() );
    }

    public void addClassLoader(final ClassLoader classLoader) {
        // don't add duplicate classloaders;
        for ( final ClassLoader cl : this.classLoaders ) {
            if ( cl == classLoader ) {
                return;
            }
        }
        this.classLoaders.add( classLoader );

        // we need to record parents for fast finding in a unique list
        ClassLoader parent = classLoader.getParent();
        for ( final ClassLoader cl : this.parents ) {
            if ( cl == parent ) {
                return;
            }
        }
        this.parents.add( parent );

    }

    public void removeClassLoader(final ClassLoader classLoader) {
        classLoaders.remove( classLoader );
    }

    public Class fastFindClass(final String name) {
        for ( final ClassLoader classLoader : this.classLoaders ) {
            final Class clazz = ((DroolsClassLoader) classLoader).fastFindClass( name );
            if ( clazz != null ) {
                return clazz;
            }
        }
        return null;
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
        Class cls = findLoadedClass( name );

        if ( cls == null ) {
            cls = fastFindClass( name );

            if ( cls == null ) {
                // now check all parents
                for ( final ClassLoader parent : this.parents ) {
                    try {
                        // due to this bug http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=2a9a78e4393c5e678a2c80f90c1?bug_id=6434149
                        cls = Class.forName( name,
                                             true,
                                             parent );
                    } catch ( ClassNotFoundException e ) {
                        // swallow
                    }
                    if ( cls != null ) {
                        break;
                    }
                }

                if ( cls == null ) {
                    return null;
                }
            }
        }

        if ( resolve ) {
            resolveClass( cls );
        }

        return cls;
    }

    public InputStream getResourceAsStream(final String name) {
        InputStream stream = super.getResourceAsStream( name );

        for ( final ClassLoader classLoader : this.classLoaders ) {
            stream = classLoader.getResourceAsStream( name );
            if ( stream != null ) {
                return stream;
            }
        }
        return stream;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class clazz = fastFindClass( name );
        if ( clazz == null ) {
            throw new ClassNotFoundException( name );
        }
        return clazz;
    }

}
