package org.drools.rule;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CompositeClassLoader extends ClassLoader
    implements
    DroolsClassLoader {
    

    private final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    private boolean hasParent = false;
    
    public CompositeClassLoader(final ClassLoader parentClassLoader) {
        super( parentClassLoader );
        if ( parentClassLoader != null ) {
            this.hasParent = true;
        }
    }

    public void addClassLoader(final ClassLoader classLoader) {
        // don't add duplicate ClasslLaders;
        for ( final ClassLoader cl : this.classLoaders ) {
            if ( cl == classLoader ) {
                return;
            }
        }
        this.classLoaders.add( classLoader );

    }

    public void removeClassLoader(final ClassLoader classLoader) {
        classLoaders.remove( classLoader );
    }

    /**
     * Search the list of child ClassLoaders
     */
    public Class fastFindClass(final String name) {
        for ( final ClassLoader classLoader : this.classLoaders ) {
            final Class cls = ((DroolsClassLoader) classLoader).fastFindClass( name );
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
    public synchronized Class loadClass(final String name,
                                        final boolean resolve) throws ClassNotFoundException {
        // search the child ClassLoaders
        Class cls = fastFindClass( name );
        
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
    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class cls = fastFindClass( name );
        
        
        if ( cls == null ) {
            throw new ClassNotFoundException( name );
        }
        return cls;
    }

}
