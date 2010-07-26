/**
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
    private final boolean           hasParent;

    public DroolsCompositeClassLoader(final ClassLoader parentClassLoader,
                                      final boolean cacheParentCalls) {
        super( parentClassLoader );
        this.hasParent = parentClassLoader != null;
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
    public Class< ? > fastFindClass(final String name) {
        for ( final ClassLoader classLoader : this.classLoaders ) {
            final Class< ? > cls = ((DroolsClassLoader) classLoader).fastFindClass( name );
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
    public Class< ? > loadClass(final String name,
                                final boolean resolve) throws ClassNotFoundException {
        // search the child ClassLoaders
        Class< ? > cls;
        cls = fastFindClass( name );

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
    protected Class< ? > findClass(final String name) throws ClassNotFoundException {
        final Class< ? > cls = fastFindClass( name );

        if ( cls == null ) {
            throw new ClassNotFoundException( name );
        }
        return cls;
    }

}
