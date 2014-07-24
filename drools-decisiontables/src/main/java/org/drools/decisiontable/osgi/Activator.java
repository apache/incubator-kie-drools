/*
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

package org.drools.decisiontable.osgi;

import java.util.Hashtable;

import org.drools.compiler.compiler.DecisionTableProvider;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.Service;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator
    implements
    BundleActivator {

    protected static final transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration kdtableReg;

    public void start(BundleContext bc) throws Exception {
        logger.info( "registering decision tables drools services" );
        this.kdtableReg = bc.registerService(  new String[]{ DecisionTableProvider.class.getName(), Service.class.getName()},
                                               new DecisionTableProviderImpl(),
                                               new Hashtable() );
        logger.info( "drools decision tables services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kdtableReg.unregister();
    }
}
