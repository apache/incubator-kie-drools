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

package org.drools.compiler.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl;
import org.drools.compiler.compiler.BPMN2ProcessProvider;
import org.drools.compiler.compiler.DecisionTableProvider;
import org.drools.core.marshalling.impl.ProcessMarshallerFactoryService;
import org.drools.core.runtime.process.ProcessRuntimeFactoryService;
import org.kie.api.Service;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.assembler.KieAssemblers;
import org.kie.internal.builder.KnowledgeBuilderFactoryService;
import org.kie.internal.runtime.KieRuntimeService;
import org.kie.internal.runtime.KieRuntimes;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.ServiceRegistry;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.kie.internal.weaver.KieWeaverService;
import org.kie.internal.weaver.KieWeavers;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator
    implements
    BundleActivator {

    protected static final transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration kbuilderReg;

    private ServiceTracker dtableTracker;
    private ServiceTracker bpmn2Tracker;
    private ServiceTracker processRuntimeTracker;
    private ServiceTracker processMarshallerTracker;
    private ServiceTracker scannerTracker;
    private ServiceTracker classResolverTracker;

    private ServiceTracker kieAssemblerServiceTracker;
    private ServiceTracker kieWeaverServiceTracker;
    private ServiceTracker kieRuntimeServiceTracker;

    public void start(BundleContext bc) throws Exception {
        logger.info( "registering compiler services" );
        this.kbuilderReg = bc.registerService( new String[]{KnowledgeBuilderFactoryService.class.getName(), Service.class.getName()},
                                               new KnowledgeBuilderFactoryServiceImpl(),
                                               new Hashtable() );

        this.dtableTracker = new ServiceTracker( bc,
                                                 bc.createFilter( "(|(" + 
                                                                  Constants.OBJECTCLASS + "=" + 
                                                                  DecisionTableProvider.class.getName() + ")(" +
                                                                  Constants.OBJECTCLASS + "=" + 
                                                                  BPMN2ProcessProvider.class.getName() +") )"),
                                                 new DroolsServiceTracker( bc,
                                                                           this ) );
        this.dtableTracker.open();

        this.bpmn2Tracker = new ServiceTracker( bc,
                                                BPMN2ProcessProvider.class.getName(),
                                                new DroolsServiceTracker( bc,
                                                                          this ) );
        this.bpmn2Tracker.open();
        
        this.processRuntimeTracker = new ServiceTracker( bc,
                                                         ProcessRuntimeFactoryService.class.getName(),
                                                         new DroolsServiceTracker( bc,
                                                                                   this ) );
        this.processRuntimeTracker.open();

        this.processMarshallerTracker = new ServiceTracker( bc,
                                                            ProcessMarshallerFactoryService.class.getName(),
                                                            new DroolsServiceTracker( bc,
                                                                                      this ) );
        this.processRuntimeTracker.open();

        this.scannerTracker = new ServiceTracker( bc,
                                                  KieScannerFactoryService.class.getName(),
                                                  new DroolsServiceTracker( bc, this ) );
        this.scannerTracker.open();

        this.classResolverTracker = new ServiceTracker( bc,
                                                        ClassLoaderResolver.class.getName(),
                                                        new DroolsServiceTracker( bc, this ) );
        this.classResolverTracker.open();
        
        this.kieAssemblerServiceTracker = new ServiceTracker( bc,
                                                              KieAssemblerService.class.getName(),
                                                              new DroolsKieComponentServiceTracker<KieAssemblerService>( bc, Activator::registerKieAssembler ) );
        this.kieAssemblerServiceTracker.open();

        this.kieWeaverServiceTracker  = new ServiceTracker( bc,
                                                            KieWeaverService.class.getName(),
                                                            new DroolsKieComponentServiceTracker<KieWeaverService>( bc, Activator::registerKieWeaver ) );
        this.kieWeaverServiceTracker.open();

        this.kieRuntimeServiceTracker = new ServiceTracker( bc,
                                                            KieRuntimeService.class.getName(),
                                                            new DroolsKieComponentServiceTracker<KieRuntimeService>( bc, Activator::registerKieRuntime ) );
        this.kieRuntimeServiceTracker.open();

        logger.info( "compiler services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbuilderReg.unregister();
        this.dtableTracker.close();
        this.bpmn2Tracker.close();
        this.processRuntimeTracker.close();
        this.processMarshallerTracker.close();
        this.scannerTracker.close();
        this.classResolverTracker.close();
        this.kieAssemblerServiceTracker.close();
        this.kieWeaverServiceTracker.close();
        this.kieRuntimeServiceTracker.close();
    }

    private static void registerKieAssembler(ServiceRegistry registry, KieAssemblerService service) {
        registry.get( KieAssemblers.class ).getAssemblers().put( service.getResourceType(), service );
    }

    private static void registerKieWeaver(ServiceRegistry registry, KieWeaverService service) {
        registry.get(KieWeavers.class).getWeavers().put(service.getResourceType(), service);
    }

    private static void registerKieRuntime(ServiceRegistry registry, KieRuntimeService service) {
        registry.get(KieRuntimes.class).getRuntimes().put(service.getServiceInterface().getName(), service);
    }

    public static class DroolsKieComponentServiceTracker<T> implements ServiceTrackerCustomizer {
        private final BundleContext bc;
        private final BiConsumer<ServiceRegistry, T> register;

        public DroolsKieComponentServiceTracker( BundleContext bc, BiConsumer<ServiceRegistry, T> register ) {
            this.bc = bc;
            this.register = register;
        }

        public Object addingService(ServiceReference ref) {
            T service = (T) this.bc.getService( ref );
            logger.info( "registering : " + service + " : " + service.getClass().getInterfaces()[0] );
            register.accept( ServiceRegistryImpl.getInstance(), service );
            return service;
        }

        public void modifiedService(ServiceReference arg0, Object arg1) { }

        public void removedService(ServiceReference ref, Object arg1) { }
    }

    public static class DroolsServiceTracker
        implements
        ServiceTrackerCustomizer {
        private final BundleContext bc;
        private final Activator     activator;
        private final Class         serviceClass;

        public DroolsServiceTracker(BundleContext bc, Activator activator) {
            this(bc, activator, null);
        }

        public DroolsServiceTracker(BundleContext bc, Activator activator, Class serviceClass) {
            this.bc = bc;
            this.activator = activator;
            this.serviceClass = serviceClass;
        }

        public Object addingService(ServiceReference ref) {
            Service service = (Service) this.bc.getService( ref );
            logger.info( "registering compiler : " + service + " : " + service.getClass().getInterfaces()[0] );

            Dictionary dic = new Hashtable();
            ServiceReference regServiceRef = this.activator.kbuilderReg.getReference();
            for ( String key : regServiceRef.getPropertyKeys() ) {
                dic.put( key,
                         regServiceRef.getProperty( key ) );
            }
            dic.put( service.getClass().getInterfaces()[0].getName(),
                     "true" );
            activator.kbuilderReg.setProperties( dic );

            ServiceRegistryImpl.getInstance().registerLocator( serviceClass != null ? serviceClass : service.getClass().getInterfaces()[0],
                                                               new BundleContextInstantiator( this.bc,
                                                                                              ref ) );
            return service;
        }

        public void modifiedService(ServiceReference arg0,
                                    Object arg1) {

        }

        public void removedService(ServiceReference ref,
                                   Object arg1) {
            Service service = (Service) bc.getService( ref );
            ServiceRegistryImpl.getInstance().unregisterLocator( service.getClass().getInterfaces()[0] );
            logger.info( "unregistering compiler : " + service + " : " + service.getClass().getInterfaces()[0] );
        }
    }

    public static class BundleContextInstantiator<V> implements Callable<V> {
        private BundleContext bc;
        private ServiceReference ref;

        public BundleContextInstantiator(BundleContext bc,
                                         ServiceReference ref) {
            this.bc = bc;
            this.ref = ref;
        }

        public V call() throws Exception {
            return (V) this.bc.getService(this.ref);
        }
    }

}
