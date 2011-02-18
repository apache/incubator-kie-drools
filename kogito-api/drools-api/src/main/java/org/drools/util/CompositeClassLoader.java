/*
 * Copyright 2010 JBoss Inc
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

package org.drools.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is an Internal Drools Class
 *
 */
public class CompositeClassLoader extends ClassLoader {
    /* Assumption: modifications are really rare, but iterations are frequent. */
    private final List<ClassLoader>       classLoaders = new CopyOnWriteArrayList<ClassLoader>();
    private final AtomicReference<Loader> loader       = new AtomicReference<Loader>();

    public CompositeClassLoader() {
        super( null );
        loader.set( new DefaultLoader() );
    }
    
    public Collection<ClassLoader> getClassLoaders() {
        return Collections.unmodifiableCollection( this.classLoaders );
    }

    public synchronized void setCachingEnabled(boolean enabled) {
        if ( enabled && loader.get() instanceof DefaultLoader ) {
            loader.set( new CachingLoader() );
        } else if ( !enabled && loader.get() instanceof CachingLoader ) {
            loader.set( DefaultLoader.INSTANCE );
        }
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
        this.classLoaders.add( 0, classLoader );
        this.loader.get().reset();
    }

    public synchronized void removeClassLoader(final ClassLoader classLoader) {
        /* synchronized to protect against concurrent runs of 
         * addClassLoader(x) and removeClassLoader(x).
         */
        classLoaders.remove( classLoader );
        this.loader.get().reset();
    }

    /**
     * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
     * and the parent ClassLoader if one is provided
     */
    public Class< ? > loadClass(final String name,
                                final boolean resolve) throws ClassNotFoundException {
        Class cls = loader.get().load( this,
                                       name,
                                       resolve );
        if ( cls == null ) {
            throw new ClassNotFoundException( "Unable to load class: " + name );
        }
        
        return cls;
    }
    
   /**
    * This ClassLoader never has classes of it's own, so only search the child ClassLoaders
    * and the parent ClassLoader if one is provided
    */
   public Class< ? > loadClass(final String name,
                               final boolean resolve,
                               final ClassLoader ignore) throws ClassNotFoundException {
       Class cls = loader.get().load( this,
                                      name,
                                      resolve,
                                      ignore );
       if ( cls == null ) {
           throw new ClassNotFoundException( "Unable to load class" + name );
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

    public void dumpStats() {
        System.out.println( loader.toString() );
    }

    private static interface Loader {
        public Class< ? > load(final CompositeClassLoader cl,
                               final String name,
                               final boolean resolve);

        public Class< ? > load(CompositeClassLoader compositeClassLoader,
                               String name,
                               boolean resolve,
                               java.lang.ClassLoader ignore);

        public void reset();
    }

    private static class DefaultLoader
        implements
        Loader {

        // this class is stateless, so lets make a singleton of it
        public static final DefaultLoader INSTANCE = new DefaultLoader();

        private DefaultLoader() {
        }

        public Class< ? > load(final CompositeClassLoader cl,
                               final String name,
                               final boolean resolve) {
            return load(cl, name, resolve, null);
        }

        public Class< ? > load(CompositeClassLoader cl,
                               String name,
                               boolean resolve,
                               ClassLoader ignore) {
            // search the child ClassLoaders
            Class< ? > cls = null;

            for ( final ClassLoader classLoader : cl.classLoaders ) {
                if ( classLoader != ignore ) {
                    if ( classLoader instanceof FastClassLoader ) {
                        cls = ((FastClassLoader)classLoader).fastFindClass( name );
                    } else {
                        // we ignore a calling classloader, to stop recursion
                        try {
                            cls = Class.forName( name,
                                                 resolve,
                                                 classLoader );
                        } catch ( ClassNotFoundException e ) {
                            // swallow as we need to check more classLoaders
                        }
                    }
                    if ( cls != null ) {
                        break;
                    }
                }
            }

            return cls;
        }
        
        public void reset() {
            // nothing to do
        }
    }

    private static class CachingLoader
        implements
        Loader {

        private final Map<String, Object> classLoaderResultMap = new HashMap<String, Object>();
        public long                       successfulCalls      = 0;
        public long                       failedCalls          = 0;
        public long                       cacheHits            = 0;

        public Class< ? > load(final CompositeClassLoader cl,
                               final String name,
                               final boolean resolve) {
            return load(cl, name, resolve, null);
        }

        public Class< ? > load(CompositeClassLoader cl,
                               String name,
                               boolean resolve,
                               ClassLoader ignore) {
            if ( classLoaderResultMap.containsKey( name ) ) {
                cacheHits++;
                return (Class< ? >) classLoaderResultMap.get( name );
            }
            // search the child ClassLoaders
            Class< ? > cls = null;

            for ( final ClassLoader classLoader : cl.classLoaders ) {
                if ( classLoader != ignore ) {
                    if ( classLoader instanceof FastClassLoader ) {
                        cls = ((FastClassLoader)classLoader).fastFindClass( name );
                    } else {
                        // we ignore a calling classloader, to stop recursion
                        try {
                            cls = Class.forName( name,
                                                 resolve,
                                                 classLoader );
                        } catch ( ClassNotFoundException e ) {
                            // swallow as we need to check more classLoaders
                        }
                    }
                    if ( cls != null ) {
                        break;
                    }
                }
            }
            if ( cls != null ) {
                classLoaderResultMap.put( name,
                                          cls );
                
                this.successfulCalls++;
            } else {
                this.failedCalls++;
            }

            return cls;
        }
        
        public void reset() {
            this.classLoaderResultMap.clear();
            this.successfulCalls = this.failedCalls = this.cacheHits = 0;
        }

        public String toString() {
            return new StringBuilder().append( "TotalCalls: " ).append( successfulCalls + failedCalls + cacheHits ).append( " CacheHits: " ).append( cacheHits ).append( " successfulCalls: " ).append( successfulCalls ).append( " FailedCalls: " ).append( failedCalls ).toString();
        }

    }

    private static class CompositeEnumeration<URL>
        implements
        Enumeration<URL> {
        private List<URL>     list;
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
    
    public CompositeClassLoader clone() {
        CompositeClassLoader classLoader = new CompositeClassLoader();
        classLoader.classLoaders.addAll( this.classLoaders );
        if ( this.loader.get() instanceof CachingLoader ) {
            classLoader.setCachingEnabled( true );
        }
        return classLoader;
    }
}
