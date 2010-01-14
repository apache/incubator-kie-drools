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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.hssf.record.formula.functions.T;
import org.drools.KnowledgeBaseProvider;
import org.drools.Service;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.io.ResourceProvider;

/**
 * This is an internal class, not for public consumption.
 *
 */
public class ServiceRegistryImpl
    implements
    ServiceRegistry {
    private static ServiceRegistry     instance        = new ServiceRegistryImpl();

    private Map<String, Callable< ? >> registry        = new HashMap<String, Callable< ? >>();
    private Map<String, Callable< ? >> defaultServices = new HashMap<String, Callable< ? >>();

    public static ServiceRegistry getInstance() {
        return ServiceRegistryImpl.instance;
    }

    public ServiceRegistryImpl() {
        init();
    }
    
    /* (non-Javadoc)
     * @see org.drools.util.internal.ServiceRegistry#registerLocator(java.lang.String, java.util.concurrent.Callable)
     */
    public synchronized void registerLocator(Class cls,
                                                 Callable cal) {
        this.registry.put( cls.getName(),
                           cal );
    }

    /* (non-Javadoc)
     * @see org.drools.util.internal.ServiceRegistry#unregisterLocator(java.lang.String)
     */
    public synchronized void unregisterLocator(Class cls) {
        this.registry.remove( cls.getName() );
        this.registry.put( cls.getName(), this.defaultServices.get( cls.getName() ) );
    }

    public synchronized <T> T get(Class<T> cls) {

        Callable< ? > cal = this.registry.get( cls.getName() );
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
        addDefault( KnowledgeBuilderProvider.class, "org.drools.builder.impl.KnowledgeBuilderProviderImpl" );
        
        addDefault( KnowledgeBaseProvider.class, "org.drools.impl.KnowledgeBaseProviderImpl" );
        
        addDefault( ResourceProvider.class,  "org.drools.io.impl.ResourceProviderImpl" );
    }
    
    private void addDefault(Class cls, String impl) {
        ReflectionInstantiator<Service> resourceRi = new ReflectionInstantiator<Service>( impl );
        registry.put( cls.getName(),
                      resourceRi );
        defaultServices.put( cls.getName(),
                             resourceRi );        
    }

    static class ReflectionInstantiator<V>
        implements
        Callable<V> {
        private String name;

        public ReflectionInstantiator(String name) {
            this.name = name;
        }

        public V call() throws Exception {
            return (V) newInstance( name );
        }
    }

    static <T> T newInstance(String name) {
        try {
            Class<T> cls = (Class<T>) Class.forName( name );
            return cls.newInstance();
        } catch ( Exception e2 ) {
            throw new IllegalArgumentException( "Unable to instantiate '" + name + "'",
                                                       e2 );
        }
    }

}
