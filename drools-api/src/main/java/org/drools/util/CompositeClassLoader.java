package org.drools.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class CompositeClassLoader extends ClassLoader {
    /* Assumption: modifications are really rare, but iterations are frequent. */
    private final List<ClassLoader> classLoaders = new CopyOnWriteArrayList<ClassLoader>();

    public CompositeClassLoader(final ClassLoader parentClassLoader) {
        super( null );
    }

    public synchronized void addClassLoader(final ClassLoader classLoader) {
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
     * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
     * and the parent ClassLoader if one is provided
     */
    public Class< ? > loadClass(final String name,
                                final boolean resolve) throws ClassNotFoundException {
        // search the child ClassLoaders
        Class< ? > cls = null;

        for ( final ClassLoader classLoader : this.classLoaders ) {
            try {
                cls = classLoader.loadClass( name );
            } catch ( ClassNotFoundException e ) {
                // swallow as we need to check more classLoaders
            }
            if ( cls != null ) {
                break;
            }
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

        return null;
    }

    @Override
    public URL getResource(String name) {
        for ( final ClassLoader classLoader : this.classLoaders ) {
            URL url = classLoader.getResource( name );
            if ( url != null ) {
                return url;
            }
        }

        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        CompositeEnumeration<URL> enumerations = new CompositeEnumeration<URL>();

        for ( final ClassLoader classLoader : this.classLoaders ) {
            Enumeration<URL> e = classLoader.getResources( name );
            if ( e != null ) {
                enumerations.addEnumeration( e );
            }
        }

        if ( enumerations.size() == 0 ) {
            return null;
        } else {
            return enumerations;
        }
    }

    private static class CompositeEnumeration<URL>
        implements
        Enumeration<URL> {
        private List<URL> list;
        private Iterator<URL> it;

        public void addEnumeration(Enumeration<URL> enumeration) {
            if ( !enumeration.hasMoreElements() ) {
                // don't add it, if it's empty
                return;
            }
            
            if ( this.it != null ) {
                throw new IllegalStateException( "cannot add more enumerations while iterator" );
            }
            
            if ( this.list == null ) {
                this.list = new ArrayList<URL>();
            }
            
            while ( enumeration.hasMoreElements() ) {
                this.list.add( enumeration.nextElement() );
            }
        }

        public int size() {
            if ( this.list == null ) {
                return 0;
            } else {
                return this.list.size();
            }
        }

        public boolean hasMoreElements() {
            if ( this.it == null ) {
                if ( this.list == null ) {
                    return false;
                } else {
                    this.it = this.list.iterator();
                }
            }
            return it.hasNext();
        }

        public URL nextElement() {
            if ( this.it == null ) {
                if ( this.list == null ) {
                    throw new NoSuchElementException();
                } else {
                    this.it = this.list.iterator();
                }
            }
            return it.next();
        }
    }
}
