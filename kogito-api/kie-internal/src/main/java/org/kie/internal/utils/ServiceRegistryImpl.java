/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.kie.internal.utils;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.api.KieServices;
import org.kie.api.Service;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.api.concurrent.KieExecutors;
import org.kie.api.io.KieResources;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.internal.assembler.KieAssemblers;
import org.kie.internal.assembler.KieAssemblersImpl;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.KieRuntimes;
import org.kie.internal.runtime.KieRuntimesImpl;
import org.kie.internal.runtime.beliefs.KieBeliefs;
import org.kie.internal.runtime.beliefs.KieBeliefsImpl;
import org.kie.internal.weaver.KieWeavers;
import org.kie.internal.weaver.KieWeaversImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an internal class, not for public consumption.
 */
public class ServiceRegistryImpl
        implements
        ServiceRegistry {
    public final String fileName = "kie.conf";
    public final String path =  "META-INF/" + fileName;

    private static final ServiceRegistry          instance        = new ServiceRegistryImpl();

    protected static final transient Logger logger          = LoggerFactory.getLogger( ServiceRegistryImpl.class );

    private final Map<String, Callable< ? >>      registry        = new HashMap<String, Callable< ? >>();
    private final Map<String, Callable< ? >>      defaultServices = new HashMap<String, Callable< ? >>();

    public static synchronized ServiceRegistry getInstance() {
        return instance;
    }

    public ServiceRegistryImpl() {
        init();
    }

    /* (non-Javadoc)
     * @see org.kie.util.internal.ServiceRegistry#registerLocator(java.lang.String, java.util.concurrent.Callable)
     */
    public synchronized void registerLocator(Class cls,
                                             Callable cal) {
        this.registry.put( cls.getName(),
                           cal );
    }

    /* (non-Javadoc)
     * @see org.kie.util.internal.ServiceRegistry#unregisterLocator(java.lang.String)
     */
    public synchronized void unregisterLocator(Class cls) {
        this.registry.remove( cls.getName() );
    }

    synchronized void registerInstance(Service service,
                                       Map map) {
        //this.context.getProperties().put( "org.dr, value )
        logger.info( "regInstance : " + map );
        String[] values = (String[]) map.get( "objectClass" );

        for ( String v : values ) {
            logger.info( v );
        }
        // logger.info( "register : " + service );
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
        //            logger.error("error", e);
        //        }
    }

    /* (non-Javadoc)
     * @see org.kie.util.internal.ServiceRegistry#unregisterLocator(java.lang.String)
     */
    synchronized void unregisterInstance(Service service,
                                         Map map) {
        logger.info( "unregister : " + map );
        String name = service.getClass().getInterfaces()[0].getName();
        this.registry.remove( name );
        this.registry.put( name,
                           this.defaultServices.get( name ) );
    }

    //    ConfigurationAdmin confAdmin;
    //    synchronized void setConfigurationAdmin(ConfigurationAdmin confAdmin) {
    //        this.confAdmin = confAdmin;
    //        logger.info( "ConfAdmin : " + this.confAdmin );
    //    }
    //
    //    synchronized void unsetConfigurationAdmin(ConfigurationAdmin confAdmin) {
    //        this.confAdmin = null;
    //    }

    //    private ComponentContext context;
    //    void activate(ComponentContext context) {
    //        logger.info( "reg comp" + context.getProperties() );
    //        this.context = context;
    //
    //
    //
    //      BundleContext bc = this.context.getBundleContext();
    //
    //      ServiceReference confAdminRef = bc.getServiceReference( ConfigurationAdmin.class.getName() );
    //      ConfigurationAdmin admin = ( ConfigurationAdmin ) bc.getService( confAdminRef );
    //      logger.info( "conf admin : " + admin );
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
            cal = this.defaultServices.get( cls.getName() );
            try {
                return cls.cast( cal.call() );
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate service for Class '" + (cls != null ? cls.getName() : null) + "'",
                                                    e );
            }
        }
    }

    private void init() {
        addDefault( "org.kie.internal.builder.KnowledgeBuilderFactoryService",
                    "org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl" );

        addDefault( "org.kie.api.builder.KnowledgeContainerFactoryService",
                    "org.drools.core.builder.impl.KnowledgeContainerFactoryServiceImpl" );

        addDefault( "org.kie.internal.KnowledgeBaseFactoryService",
                    "org.drools.core.impl.KnowledgeBaseFactoryServiceImpl" );

        addDefault( KieResources.class,
                    "org.drools.core.io.impl.ResourceFactoryServiceImpl" );

        addDefault(  KieMarshallers.class,
                     "org.drools.core.marshalling.impl.MarshallerProviderImpl");
        addDefault(  KieExecutors.class,
                     "org.drools.core.concurrent.ExecutorProviderImpl");
        addDefault(  KieServices.class,
                     "org.drools.compiler.kie.builder.impl.KieServicesImpl");
        addDefaultFactory( KieScannerFactoryService.class,
                           "org.kie.scanner.KieScannerFactoryServiceImpl");
        addDefault( KieStoreServices.class,
                    "org.drools.persistence.jpa.KnowledgeStoreServiceImpl");
        addDefault( CorrelationKeyFactory.class,
                    "org.jbpm.persistence.correlation.JPACorrelationKeyFactory");
        addDefault( ClassLoaderResolver.class,
                    "org.kie.scanner.MavenClassLoaderResolver" );
        addDefault( ServiceDiscovery.class,
                    "org.drools.core.util.ServiceDiscoveryImpl" );


        defaultServices.put( KieAssemblers.class.getName(),
                             new ReturnInstance( new KieAssemblersImpl()) );
        defaultServices.put( KieWeavers.class.getName(),
                             new ReturnInstance( new KieWeaversImpl()) );
        defaultServices.put( KieRuntimes.class.getName(),
                             new ReturnInstance( new KieRuntimesImpl()) );
        defaultServices.put( KieBeliefs.class.getName(),
                             new ReturnInstance( new KieBeliefsImpl()) );

        initServiceDiscovery();
    }

    private void initServiceDiscovery() {
        Enumeration<URL> confResources = null;
        try {
            confResources = getClassLoader().getResources( path );
        } catch ( Exception e ) {
            // no conf file or no ServiceDiscovery - ignore
        }
        if (confResources != null && confResources.hasMoreElements()) {
            get( ServiceDiscovery.class ).discoverFactories( confResources, this );
        }
    }

    public synchronized void addDefault(Class cls,
                                        String impl) {
        addDefault(cls.getName(), impl);
    }

    private synchronized void addDefault(String service,
                                        String impl) {
        ReflectionInstantiator<Service> resourceRi = new ReflectionInstantiator<Service>( impl );
        defaultServices.put( service,
                             resourceRi );
    }

    public synchronized void addDefaultFactory(Class cls,
                                               String impl) {
        addDefaultFactory(cls.getName(), impl);
    }

    private synchronized void addDefaultFactory(String service,
                                                String impl) {
        FactoryInstantiator<Service> resourceRi = new FactoryInstantiator<Service>( impl );
        defaultServices.put(service,
                            resourceRi);
    }

    public static class ReflectionInstantiator<V>
            implements
            Callable<V> {
        private final String name;

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

    public static class FactoryInstantiator<V>
            implements
            Callable<V> {
        private final String name;
        private final AtomicReference<V> service = new AtomicReference<V>();

        public FactoryInstantiator(String name) {
            this.name = name;
        }

        public V call() throws Exception {
            if (service.get() == null) {
                try {
                    Class<V> cls = (Class<V>) Class.forName( name );
                    service.compareAndSet(null, cls.newInstance());
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "Unable to instantiate '" + name + "'",
                                                        e );
                }
            }
            return service.get();
        }
    }

    public static class ReturnInstance<V>
            implements
            Callable<V> {
        private final Service service;

        public ReturnInstance(Service service) {
            this.service = service;
        }

        public V call() throws Exception {
            return (V) service;
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        if (cl == null) {
            cl = ClassLoaderUtil.class.getClassLoader();
        }
        return cl;
    }
}
