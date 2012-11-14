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

package org.jbpm.osgi.bpmn2;

import java.util.Hashtable;

import org.kie.Service;
import org.drools.compiler.BPMN2ProcessProvider;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration bpmn2ProcessReg;

    @SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
        this.bpmn2ProcessReg = bc.registerService( new String[]{ BPMN2ProcessProvider.class.getName(), Service.class.getName()},
                                                   new BPMN2ProcessProviderImpl(),
                                                   new Hashtable() );
    }

    public void stop(BundleContext bc) throws Exception {
        this.bpmn2ProcessReg.unregister();
    }

}
