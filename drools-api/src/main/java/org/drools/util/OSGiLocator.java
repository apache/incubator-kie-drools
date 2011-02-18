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

/*
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Maintains a mapping of service names to an ordered set of service
 * providers when running in an OSGi container.
 * <p/>
 * It is expected that a bundle using Drools will populate this map
 * with properties from its own ClassLoader.
 * <p/>
 * This is an adaptation of the technique described by Guillaume Nodet
 * in his article<i>
 * <a href='http://gnodet.blogspot.com/2008/05/jee-specs-in-osgi.html'>
 * Java EE specs in OSGi</a></i>. The main changes were to add comments.
 *
 * @author Guillaume Nodet
 * @author Faron Dutton
 * @see {@linkplain http://gnodet.blogspot.com/2008/05/jee-specs-in-osgi.html}
 */
public final class OSGiLocator {

    /**
     * Maps a service name (the fully qualified name of the interface)
     * to an ordered set of factories. Each factory instantiating
     * a specific service provider (implementation).
     */
    private static Map<String, List<Callable<Class< ? >>>> factories;

    /**
     * Private constructor used to prevent instantiation of this
     * utility class (i.e., Utility Pattern).
     */
    private OSGiLocator() {
        super();
    }

    /**
     * Removes the given service provider factory from the set of
     * providers for the service.
     * 
     * @param serviceName
     *          The fully qualified name of the service interface.
     * @param factory
     *          A factory for creating a specific type of service
     *          provider. May be <tt>null</tt> in which case this
     *          method does nothing.
     * @throws IllegalArgumentException if serviceName is <tt>null</tt>
     */
    public static synchronized void unregister(final String serviceName,
                                               final Callable<Class< ? >> factory) {
        if ( serviceName == null ) {
            throw new IllegalArgumentException( "serviceName cannot be null" );
        }
        if ( factories != null ) {
            List<Callable<Class< ? >>> l = factories.get( serviceName );
            if ( l != null ) {
                l.remove( factory );
            }
        }
    }

    /**
     * Adds the given service provider factory to the set of
     * providers for the service.
     * 
     * @param serviceName
     *          The fully qualified name of the service interface.
     * @param factory
     *          A factory for creating a specific type of service
     *          provider. May be <tt>null</tt> in which case this
     *          method does nothing.
     * @throws IllegalArgumentException if serviceName is <tt>null</tt>
     */
    public static synchronized void register(final String serviceName,
                                             final Callable<Class< ? >> factory) {
        if ( serviceName == null ) {
            throw new IllegalArgumentException( "serviceName cannot be null" );
        }
        if ( factory != null ) {
            if ( factories == null ) {
                factories = new HashMap<String, List<Callable<Class< ? >>>>();
            }
            List<Callable<Class< ? >>> l = factories.get( serviceName );
            if ( l == null ) {
                l = new ArrayList<Callable<Class< ? >>>();
                factories.put( serviceName,
                               l );
            }
            l.add( factory );
        }
    }

    /**
     * Finds the preferred provider for the given service. The preferred
     * provider is the last one added to the set of providers.
     * 
     * @param serviceName
     *          The fully qualified name of the service interface.
     * @return
     *          The last provider added for the service if any exists.
     *          Otherwise, it returns <tt>null</tt>.
     * @throws IllegalArgumentException if serviceName is <tt>null</tt>
     */
    public static synchronized Class< ? > locate(final String serviceName) {
        if ( serviceName == null ) {
            throw new IllegalArgumentException( "serviceName cannot be null" );
        }
        if ( factories != null ) {
            List<Callable<Class< ? >>> l = factories.get( serviceName );
            if ( l != null && !l.isEmpty() ) {
                Callable<Class< ? >> c = l.get( l.size() - 1 );
                try {
                    return c.call();
                } catch ( Exception e ) {
                }
            }
        }
        return null;
    }

    /**
     * Finds all providers for the given service.
     * 
     * @param serviceName
     *          The fully qualified name of the service interface.
     * @return
     *          The ordered set of providers for the service if any exists.
     *          Otherwise, it returns an empty list.
     * @throws IllegalArgumentException if serviceName is <tt>null</tt>
     */
    public static synchronized List<Class< ? >> locateAll(final String serviceName) {
        if ( serviceName == null ) {
            throw new IllegalArgumentException( "serviceName cannot be null" );
        }
        List<Class< ? >> classes = new ArrayList<Class< ? >>();
        if ( factories != null ) {
            List<Callable<Class< ? >>> l = factories.get( serviceName );
            if ( l != null ) {
                for ( Callable<Class< ? >> c : l ) {
                    try {
                        classes.add( c.call() );
                    } catch ( Exception e ) {
                    }
                }
            }
        }
        return classes;
    }

}