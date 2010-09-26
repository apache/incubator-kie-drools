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

package org.drools.osgi.core;

import java.util.Dictionary;
import java.util.Hashtable;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.Service;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.io.ResourceFactoryService;
import org.drools.io.impl.ResourceFactoryServiceImpl;
import org.drools.marshalling.MarshallerProvider;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.drools.osgi.api.Activator.BundleContextInstantiator;
import org.drools.util.ServiceRegistryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration resourceReg;
    private ServiceRegistration kbaseReg;
    private ServiceRegistration marshallerProviderReg;

    public void start(BundleContext bc) throws Exception {
        System.out.println( "registering core  services" );
        this.resourceReg = bc.registerService( new String[]{ResourceFactoryService.class.getName(), Service.class.getName()},
                                               new ResourceFactoryServiceImpl(),
                                               new Hashtable() );
        
        this.kbaseReg = bc.registerService( new String[]{KnowledgeBaseFactoryService.class.getName(), Service.class.getName()},
                                            new KnowledgeBaseFactoryServiceImpl(),
                                            new Hashtable() );

        this.marshallerProviderReg = bc.registerService( new String[]{MarshallerProvider.class.getName(), Service.class.getName()},
                new MarshallerProviderImpl(),
                new Hashtable() );
        
        System.out.println( "core services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbaseReg.unregister();
        this.resourceReg.unregister();
        this.marshallerProviderReg.unregister();
    }
    
}
