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

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.Service;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerService;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.io.ResourceFactoryService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

/**
 * This is an internal class, not for public consumption.
 *
 */
public class ServiceRegistryImpl
    implements
    ServiceRegistry {
    private static ServiceRegistry     instance;

    private Map<String, Callable< ? >> registry        = new HashMap<String, Callable< ? >>();
    private Map<String, Callable< ? >> defaultServices = new HashMap<String, Callable< ? >>();

    public static synchronized ServiceRegistry getInstance() {
        if ( instance == null ) {
            instance = new ServiceRegistryImpl();
        }
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
        this.registry.put( cls.getName(),
                           this.defaultServices.get( cls.getName() ) );
    }
    
    synchronized void registerInstance(Service service, Map map) {  
        //this.context.getProperties().put( "org.dr, value )
        System.out.println( "regInstance : " + map );
        String[] values = ( String[] ) map.get( "objectClass" );        

        for ( String v : values ) {
            System.out.println( v );
        }
       // System.out.println( "register : " + service );
        this.registry.put( service.getClass().getInterfaces()[0].getName(),
                           new ReturnInstance<Service>( service ) );
        

//        
//        BundleContext bc = this.context.getBundleContext();
//        ServiceReference confAdminRef = bc.getServiceReference( ConfigurationAdmin.class.getName() );
//        ConfigurationAdmin admin = ( ConfigurationAdmin ) bc.getService( confAdminRef );
//        
//        try {
//            Configuration conf = admin.getConfiguration( (String) confAdminRef.getProperty( "service.id" ) );
//            Dictionary properties = conf.getProperties();
//            properties.put( values[0], "true" );
//            conf.update( properties );
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        }
    }

    /* (non-Javadoc)
     * @see org.drools.util.internal.ServiceRegistry#unregisterLocator(java.lang.String)
     */
    synchronized void unregisterInstance(Service service, Map map) {
        System.out.println( "unregister : " + map );
        String name = service.getClass().getInterfaces()[0].getName();
        this.registry.remove( name );
        this.registry.put( name,
                           this.defaultServices.get( name ) );
    }    
    
//    ConfigurationAdmin confAdmin;
//    synchronized void setConfigurationAdmin(ConfigurationAdmin confAdmin) {
//        this.confAdmin = confAdmin;
//        System.out.println( "ConfAdmin : " + this.confAdmin );
//    }   
//    
//    synchronized void unsetConfigurationAdmin(ConfigurationAdmin confAdmin) {
//        this.confAdmin = null;
//    }    
    
//    private ComponentContext context;
//    void activate(ComponentContext context) {
//        System.out.println( "reg comp" + context.getProperties() );
//        this.context = context;
//        
//       
//        
//      BundleContext bc = this.context.getBundleContext();
//      
//      ServiceReference confAdminRef = bc.getServiceReference( ConfigurationAdmin.class.getName() );
//      ConfigurationAdmin admin = ( ConfigurationAdmin ) bc.getService( confAdminRef );     
//      System.out.println( "conf admin : " + admin );
//        //context.
//    //    log = (LogService) context.locateService("LOG");
//        }
//    void deactivate(ComponentContext context ){
//        
//    }

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
        addDefault( KnowledgeBuilderFactoryService.class,
                    "org.drools.builder.impl.KnowledgeBuilderFactoryServiceImpl" );

        addDefault( KnowledgeBaseFactoryService.class,
                    "org.drools.impl.KnowledgeBaseFactoryServiceImpl" );

        addDefault( ResourceFactoryService.class,
                    "org.drools.io.impl.ResourceFactoryServiceImpl" );
        
        addDefault(  SystemEventListenerService.class,
                     "org.drools.impl.SystemEventListenerServiceImpl" );
        
        
//        addDefault( SystemE.class,
//        "org.drools.io.impl.ResourceFactoryServiceImpl" );        
    }

    private void addDefault(Class cls,
                            String impl) {
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

    static class ReturnInstance<V>
        implements
        Callable<V> {
        private Service service;

        public ReturnInstance(Service service) {
            this.service = service;
        }

        public V call() throws Exception {
            return (V) service;
        }
    }



}
