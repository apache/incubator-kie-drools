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

package org.drools.util.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.KnowledgeBaseProvider;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.io.ResourceProvider;

/**
 * This is an internal class, not for public consumption.
 *
 */
public class serviceLocatorImpl {
    private static serviceLocatorImpl            instance         = new serviceLocatorImpl();

    private Map<String, Callable<Class< ? >>> serviceFactories = new HashMap<String, Callable<Class< ? >>>();

    public static serviceLocatorImpl getInstance() {
        return serviceLocatorImpl.instance;
    }
    
    public synchronized void registerLocator(String name, Callable<Class< ? >> cal) {
        this.serviceFactories.put( name, cal );
    }
    
    public synchronized void unregisterLocator(String name) {
        this.serviceFactories.remove( name );
    }

    public synchronized <T> T locate(Class<T> cls) {

        Callable<Class< ? >> cal = this.serviceFactories.get( cls.getName() );
        if ( cal != null ) {
            try {
                return cls.cast( cal.call() );
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate service for Class '" + (cls != null ? cls.getName() : null) + "'",
                                                    e );
            }
        } else {
            throw new IllegalArgumentException( "Unable to locate a service for Class '" + (cls != null ? cls.getName() : null) + "'" );
        }
    }

    private void init() {
        serviceFactories.put( KnowledgeBuilderProvider.class.getName(),
                              null );
        serviceFactories.put( KnowledgeBaseProvider.class.getName(),
                              null );
        serviceFactories.put( ResourceProvider.class.getName(),
                              null );
    }

}
