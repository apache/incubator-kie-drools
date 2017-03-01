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

package org.kie.dmn.core.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.runtime.DMNRuntimeService;
import org.kie.dmn.core.weaver.DMNWeaverService;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.runtime.KieRuntimeService;
import org.kie.internal.weaver.KieWeaverService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    protected static final transient Logger logger;
    private ServiceRegistration assemblerService;
    private ServiceRegistration weaverService;
    private ServiceRegistration runtimeService;

    public void start(final BundleContext bc) throws Exception {
        logger.info("registering Kie DMN services");
        this.assemblerService = bc.registerService(new String[]{DMNAssemblerService.class.getName(), KieAssemblerService.class.getName()}, (Object) new DMNAssemblerService(), (Dictionary) new Hashtable());
        this.weaverService = bc.registerService(new String[]{DMNWeaverService.class.getName(), KieWeaverService.class.getName()}, (Object) new DMNWeaverService(), (Dictionary) new Hashtable());
        this.runtimeService = bc.registerService(new String[]{DMNRuntimeService.class.getName(), KieRuntimeService.class.getName()}, (Object) new DMNRuntimeService(), (Dictionary) new Hashtable());
        logger.info("Kie DMN services registered");
    }

    public void stop(final BundleContext bc) throws Exception {
        this.assemblerService.unregister();
        this.weaverService.unregister();
        this.runtimeService.unregister();
    }

    static {
        logger = LoggerFactory.getLogger((Class) Activator.class);
    }
}
