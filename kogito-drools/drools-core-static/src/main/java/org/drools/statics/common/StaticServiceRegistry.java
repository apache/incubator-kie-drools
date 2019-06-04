/*
 * Copyright 2005 JBoss Inc
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

package org.drools.statics.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.statics.common.SimpleInstanceCreator.constructor;
import static org.drools.statics.common.SimpleInstanceCreator.instance;

public class StaticServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(StaticServiceRegistry.class);

    static final StaticServiceRegistry INSTANCE = new StaticServiceRegistry();

    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    private Map<String, Constructor<?>> constructorMap = new HashMap<>();

    StaticServiceRegistry() {
        wireServices();
    }

    private void wireServices() {
        serviceMap.put(org.kie.api.io.KieResources.class, instance("org.drools.core.io.impl.ResourceFactoryServiceImpl"));
        serviceMap.put(org.kie.api.marshalling.KieMarshallers.class, instance("org.drools.core.marshalling.impl.MarshallerProviderImpl"));
        serviceMap.put(org.kie.api.concurrent.KieExecutors.class, instance("org.drools.core.concurrent.ExecutorProviderImpl"));
        serviceMap.put(org.kie.api.KieServices.class, instance("org.drools.compiler.kie.builder.impl.KieServicesImpl"));
        serviceMap.put(org.kie.internal.builder.KnowledgeBuilderFactoryService.class, instance("org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl"));
        serviceMap.put(KieAssemblers.class, new StaticKieAssemblers());

        registerService("org.drools.compiler.kie.builder.impl.InternalKieModuleProvider", "org.drools.modelcompiler.CanonicalKieModuleProvider", true);
        registerService("org.drools.compiler.compiler.DecisionTableProvider", "org.drools.decisiontable.DecisionTableProviderImpl", false);

        constructorMap.put("TimerService", constructor("org.kie.services.time.impl.JDKTimerService"));
    }

    private void registerService(String service, String implementation, boolean mandatory) {
        try {
            serviceMap.put(Class.forName(service), instance(implementation));
        } catch (Exception e) {
            if (mandatory) {
                throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
            } else {
                log.debug("Ignored non-mandatory service load error", e);
            }
        }
    }

    @Override
    public <T> T get(Class<T> cls) {
        return (T) serviceMap.get(cls);
    }

    public <T> T newInstance(String name) {
        try {
            return (T) constructorMap.get(name).newInstance();
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    static class StaticKieAssemblers implements KieAssemblers {

        private Map<ResourceType, KieAssemblerService> assemblers = new HashMap<>();

        public StaticKieAssemblers() {
            // insert here reflective instantiation to assembler services
            // this is an ad-interim solution
            // e.g. assemblers.put(ResourceType.DMN, (KieAssemblerService)
            //         instance("org.kie.dmn.core.assembler.DMNAssemblerService"));
        }

        @Override
        public void addResource(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
            KieAssemblerService assembler = assemblers.get(type);
            if (assembler != null) {
                assembler.addResource(knowledgeBuilder,
                                      resource,
                                      type,
                                      configuration);
            } else {
                log.debug("KieAssemblers: ignored " + type);
            }
        }

        @Override
        public void addResources(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
            KieAssemblerService assembler = assemblers.get(type);
            if (assembler != null) {
                assembler.addResources(knowledgeBuilder, resources, type);
            } else {
                log.debug("KieAssemblers: ignored " + type);
            }
        }
    }
}
