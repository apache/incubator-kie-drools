package org.drools.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.rule.PackageCompilationData.PackageClassLoader;

public class CompositePackageClassLoader extends ClassLoader {

    private final List classLoaders = new ArrayList();

    public CompositePackageClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
    }

    public void addClassLoader(final ClassLoader classLoader) {
        this.classLoaders.add( classLoader );
    }

    public void removeClassLoader(final ClassLoader classLoader) {
        for ( final Iterator it = this.classLoaders.iterator(); it.hasNext(); ) {
            if ( it.next() == classLoader ) {
                it.remove();
                break;
            }
        }
    }

    private Class compositeFastFindClass(final String name) {
        for ( final Iterator it = this.classLoaders.iterator(); it.hasNext(); ) {
            final PackageClassLoader classLoader = (PackageClassLoader) it.next();
            final Class clazz = classLoader.fastFindClass( name );
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
        Class clazz = findLoadedClass( name );

        if ( clazz == null ) {
            clazz = compositeFastFindClass( name );

            if ( clazz == null ) {

                final ClassLoader parent = getParent();
                if ( parent != null ) {
                    clazz = parent.loadClass( name );
                } else {
                    throw new ClassNotFoundException( name );
                }
            }
        }

        if ( resolve ) {
            resolveClass( clazz );
        }

        return clazz;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class clazz = compositeFastFindClass( name );
        if ( clazz == null ) {
            throw new ClassNotFoundException( name );
        }
        return clazz;
    }

}
