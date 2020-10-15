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

package org.drools.statics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.services.KieWeaversImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(StaticServiceRegistry.class);

    static final StaticServiceRegistry INSTANCE = new StaticServiceRegistry();

    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    private Map<String, Constructor<?>> constructorMap = new HashMap<>();

    StaticServiceRegistry() {
        wireServices();
    }

    private void wireServices() {
        serviceMap.put(org.kie.api.io.KieResources.class, SimpleInstanceCreator.instance("org.drools.core.io.impl.ResourceFactoryServiceImpl"));
        serviceMap.put(org.kie.api.concurrent.KieExecutors.class, SimpleInstanceCreator.instance("org.drools.core.concurrent.ExecutorProviderImpl"));
        serviceMap.put(org.kie.api.KieServices.class, SimpleInstanceCreator.instance("org.drools.compiler.kie.builder.impl.KieServicesImpl"));
        serviceMap.put(org.kie.internal.builder.KnowledgeBuilderFactoryService.class, SimpleInstanceCreator.instance("org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl"));
        serviceMap.put(org.kie.kogito.rules.DataSource.Factory.class, SimpleInstanceCreator.instance("org.kie.kogito.rules.units.impl.DataSourceFactoryImpl"));
        serviceMap.put(org.kie.internal.ruleunit.RuleUnitComponentFactory.class, SimpleInstanceCreator.instance("org.kie.kogito.rules.units.impl.RuleUnitComponentFactoryImpl"));
        serviceMap.put(KieAssemblers.class, new StaticKieAssemblers());
        serviceMap.put(KieRuntimes.class, SimpleInstanceCreator.instance("org.kie.internal.services.KieRuntimesImpl"));
        serviceMap.put(KieWeavers.class, new KieWeaversImpl());

        registerService("org.drools.compiler.kie.builder.impl.InternalKieModuleProvider", "org.drools.modelcompiler.CanonicalKieModuleProvider", true);
        registerService("org.drools.compiler.compiler.DecisionTableProvider", "org.drools.decisiontable.DecisionTableProviderImpl", false);
        registerService("org.drools.core.reteoo.KieComponentFactoryFactory", "org.drools.core.kogito.factory.KogitoComponentFactoryFactory", true);

        registerService("org.drools.core.marshalling.impl.ProcessMarshallerFactoryService", "org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl", false);
        registerService("org.drools.core.runtime.process.ProcessRuntimeFactoryService", "org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl", false);

        constructorMap.put("TimerService", SimpleInstanceCreator.constructor("org.drools.core.time.impl.JDKTimerService"));

        // pmml
        registerKieRuntimeService("org.kie.pmml.evaluator.api.executor.PMMLRuntime", "org.kie.pmml.evaluator.core.service.PMMLRuntimeService", false);
        registerKieWeaverService("org.kie.pmml.evaluator.assembler.PMMLWeaverService", false);

        // marshalling
        registerService(org.kie.api.marshalling.KieMarshallers.class.getCanonicalName(), "org.drools.serialization.protobuf.MarshallerProviderImpl", false);
        registerService("org.drools.compiler.kie.builder.impl.CompilationCacheProvider", "org.drools.serialization.protobuf.CompilationCacheProviderImpl", false);
    }

    private void registerService(String service, String implementation, boolean mandatory) {
        try {
            serviceMap.put(Class.forName(service), SimpleInstanceCreator.instance(implementation));
        } catch (Exception e) {
            commonManageException(service, e, mandatory);
        }
    }

    private void registerKieRuntimeService(String runtimeName, String kieRuntimeServiceImplementation, boolean mandatory) {
        try {
            KieRuntimeService kieRuntimeService = (KieRuntimeService)SimpleInstanceCreator.instance(kieRuntimeServiceImplementation);
            ((KieRuntimes) serviceMap.get(KieRuntimes.class)).getRuntimes().put(runtimeName, kieRuntimeService);
        } catch (Exception e) {
            commonManageException("KieRuntimes", e, mandatory);
        }
    }

    private void registerKieWeaverService(String kieWeaverServiceImplementation, boolean mandatory) {
        try {
            final KieWeaversImpl kieWeavers = (KieWeaversImpl) serviceMap.get(KieWeavers.class);
            KieWeaverService kieWeaverService = (KieWeaverService) SimpleInstanceCreator.instance(kieWeaverServiceImplementation);
            kieWeavers.accept(kieWeaverService);
        } catch (Exception e) {
            commonManageException("KieWeaverService", e, mandatory);
        }
    }

    private void commonManageException(String ignoredServiceType, Exception e, boolean mandatory ) {
        if (mandatory) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } else {
            log.debug("Ignored non-mandatory {} service load error", ignoredServiceType, e);
        }
    }

    @Override
    public <T> T get(Class<T> cls) {
        return (T) serviceMap.get(cls);
    }

    @Override
    public <T> List<T> getAll( Class<T> cls ) {
        return Collections.singletonList( get(cls) );
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
                log.debug("KieAssemblers: ignored {}", type);
            }
        }

        @Override
        public void addResources(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
            KieAssemblerService assembler = assemblers.get(type);
            if (assembler != null) {
                assembler.addResources(knowledgeBuilder, resources, type);
            } else {
                log.debug("KieAssemblers: ignored {}", type);
            }
        }
    }
}
