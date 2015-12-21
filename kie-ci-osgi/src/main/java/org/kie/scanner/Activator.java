/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import org.kie.api.Service;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.internal.utils.ClassLoaderResolver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    protected static final transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration scannerReg;
    private ServiceRegistration classResolverReg;

    @Override
    public void start(BundleContext context) throws Exception {
        logger.info( "registering kiescanner services" );
        this.scannerReg = context.registerService( new String[]{ KieScannerFactoryService.class.getName(), Service.class.getName() },
                                                   new KieScannerFactoryServiceImpl(),
                                                   new Hashtable() );
        this.classResolverReg = context.registerService( new String[]{ ClassLoaderResolver.class.getName(), Service.class.getName() },
                                                         new MavenClassLoaderResolver(),
                                                         new Hashtable() );
        logger.info( "kiescanner services registered" );
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.scannerReg.unregister();
        this.classResolverReg.unregister();
    }
}
