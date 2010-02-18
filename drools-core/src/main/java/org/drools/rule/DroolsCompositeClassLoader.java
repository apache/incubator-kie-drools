package org.drools.rule;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.core.util.DroolsClassLoader;

/**
 * A classloader that loads from a (dynamic) list of sub-classloaders.
 */
public class DroolsCompositeClassLoader extends ClassLoader
    implements
    DroolsClassLoader {
    

    /* Assumption: modifications are really rare, but iterations are frequent. */
    private final List<ClassLoader> classLoaders = new CopyOnWriteArrayList<ClassLoader>();
    private boolean hasParent = false;
    
    public DroolsCompositeClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        if ( parentClassLoader != null ) {
            this.hasParent = true;
        }
    }

    public synchronized void addClassLoader(final ClassLoader classLoader) {
        if ( classLoader == null ) {
            return;
        }
        /* NB: we need synchronized here even though we use a COW list:
         *     two threads may try to add the same new class loader, so we need
         *     to protect over a bigger area than just a single iteration.
         */
        // don't add duplicate ClassLoaders;
        for ( final ClassLoader cl : this.classLoaders ) {
            if ( cl == classLoader ) {
                return;
            }
        }
        this.classLoaders.add( classLoader );

    }

    public synchronized void removeClassLoader(final ClassLoader classLoader) {
        /* synchronized to protect against concurrent runs of 
         * addClassLoader(x) and removeClassLoader(x).
         */
        classLoaders.remove( classLoader );
    }

    /**
     * Search the list of child ClassLoaders
     */
    public Class<?> fastFindClass(final String name) {
        for ( final ClassLoader classLoader : this.classLoaders ) {
            final Class<?> cls = ((DroolsClassLoader) classLoader).fastFindClass( name );
            if ( cls != null ) {
                return cls;
            }
        }
        return null;
    }

    /**
     * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
     * and the parent ClassLoader if one is provided
     */ 
    public Class<?> loadClass(final String name,
                                        final boolean resolve) throws ClassNotFoundException {
        // search the child ClassLoaders
        Class<?> cls = fastFindClass( name );
        
        // still not found so search the parent ClassLoader
        if ( this.hasParent && cls == null ) {
            cls = Class.forName( name,
                           true,
                           getParent() );
        }        

        if ( resolve ) {
            resolveClass( cls );
        }

        return cls;
    }

    /**
     * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
     * and the parent ClassLoader if one is provided
     */    
    public InputStream getResourceAsStream(final String name) {        
        for ( final ClassLoader classLoader : this.classLoaders ) {
            InputStream stream = classLoader.getResourceAsStream( name );
            if ( stream != null ) {
                return stream;
            }
        }
        
        if ( this.hasParent ) {
            return getParent().getResourceAsStream( name );            
        }
        
        return null;

    }
    

    /**
     * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
     */    
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        final Class<?> cls = fastFindClass( name );
        
        if ( cls == null ) {
            throw new ClassNotFoundException( name );
        }
        return cls;
    }

}
