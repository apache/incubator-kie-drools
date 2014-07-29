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

package org.jbpm.osgi.services.task;

import java.util.Hashtable;

import org.jbpm.services.task.persistence.TaskModelProviderImpl;
import org.kie.api.Service;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskModelProviderService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {

	private ServiceRegistration taskModelFactoryReg;
    
    public void start(BundleContext bc) throws Exception {
        this.taskModelFactoryReg = bc.registerService( new String[]{ TaskModelProviderService.class.getName(), Service.class.getName()},
                                                                     new TaskModelProviderImpl(),
                                                                     new Hashtable() );
    }

    public void stop(BundleContext bc) throws Exception {
        this.taskModelFactoryReg.unregister();
        
    }

}
