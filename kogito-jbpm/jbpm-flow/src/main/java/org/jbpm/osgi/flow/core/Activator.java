/**
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

package org.jbpm.osgi.flow.core;

import org.drools.core.marshalling.impl.ProcessMarshallerFactoryService;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactoryService;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.kie.api.Service;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration processRuntimeReg;
    
    private ServiceRegistration processRuntimeReg2;
    
    public void start(BundleContext bc) throws Exception {
        this.processRuntimeReg = bc.registerService( new String[]{ ProcessRuntimeFactoryService.class.getName(), Service.class.getName()},
                                                                   new ProcessRuntimeFactoryServiceImpl(),
                                                                   new Hashtable() );
        this.processRuntimeReg2 = bc.registerService( new String[]{ ProcessMarshallerFactoryService.class.getName(), Service.class.getName()},
                                                                   new ProcessMarshallerFactoryServiceImpl(),
                                                                   new Hashtable() );
        ProcessRuntimeFactory.reInitializeProvider();
    }

    public void stop(BundleContext bc) throws Exception {
        this.processRuntimeReg.unregister();
        this.processRuntimeReg2.unregister();
        
    }

}
