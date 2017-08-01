/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.osgi;

import org.kie.api.internal.utils.ServiceDiscoveryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class BaseActivator implements BundleActivator {

    private final ClassLoader classLoader;

    private ServiceTracker serviceDiscoveryTracker;

    protected BaseActivator( ClassLoader classLoader ) {
        this.classLoader = classLoader;
    }

    public void start(BundleContext context ) throws Exception {
        this.serviceDiscoveryTracker  = new ServiceTracker( context,
                                                            ServiceDiscoveryImpl.class.getName(),
                                                            new ServiceDiscoveryTracker( context, classLoader ) );
        this.serviceDiscoveryTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.serviceDiscoveryTracker.close();
    }

    public static class ServiceDiscoveryTracker implements ServiceTrackerCustomizer<ServiceDiscoveryImpl, ServiceDiscoveryImpl> {
        private final BundleContext context;
        private ClassLoader classLoader;

        public ServiceDiscoveryTracker( BundleContext context, ClassLoader classLoader ) {
            this.context = context;
            this.classLoader = classLoader;
        }

        @Override
        public ServiceDiscoveryImpl addingService(ServiceReference<ServiceDiscoveryImpl> ref ) {
            ServiceDiscoveryImpl service = context.getService( ref );
            service.registerConfs( classLoader, classLoader.getResource( "META-INF/kie.conf") );
            return service;
        }

        @Override
        public void modifiedService( ServiceReference<ServiceDiscoveryImpl> reference, ServiceDiscoveryImpl service ) { }

        @Override
        public void removedService( ServiceReference<ServiceDiscoveryImpl> reference, ServiceDiscoveryImpl service ) { }
    }
}
