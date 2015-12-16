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

package org.drools.core.osgi;

import java.util.Hashtable;

import org.drools.core.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.core.io.impl.ResourceFactoryServiceImpl;
import org.drools.core.marshalling.impl.MarshallerProviderImpl;
import org.kie.internal.KnowledgeBaseFactoryService;
import org.kie.api.Service;
import org.kie.api.io.KieResources;
import org.kie.api.marshalling.KieMarshallers;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator
    implements
    BundleActivator {

    protected static final transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration resourceReg;
    private ServiceRegistration kbaseReg;
    private ServiceRegistration marshallerProviderReg;

    public void start(BundleContext bc) throws Exception {
        logger.info( "registering core  services" );
        this.resourceReg = bc.registerService( new String[]{KieResources.class.getName(), Service.class.getName()},
                                               new ResourceFactoryServiceImpl(),
                                               new Hashtable() );
        
        this.kbaseReg = bc.registerService( new String[]{KnowledgeBaseFactoryService.class.getName(), Service.class.getName()},
                                            new KnowledgeBaseFactoryServiceImpl(),
                                            new Hashtable() );

        this.marshallerProviderReg = bc.registerService( new String[]{KieMarshallers.class.getName(), Service.class.getName()},
                new MarshallerProviderImpl(),
                new Hashtable() );

        logger.info( "core services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbaseReg.unregister();
        this.resourceReg.unregister();
        this.marshallerProviderReg.unregister();
    }
    
}
