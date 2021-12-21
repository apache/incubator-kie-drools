/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.wiring.statics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.internal.services.KieWeaversImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(StaticServiceRegistry.class);

    static final StaticServiceRegistry INSTANCE = new StaticServiceRegistry();

    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    private Map<String, Constructor<?>> constructorMap = new HashMap<>();

    protected StaticServiceRegistry() {
        wireServices();
    }

    protected void wireServices() {
        registerService("org.drools.wiring.api.ComponentsSupplier", "org.drools.wiring.statics.StaticComponentsSupplier", true);

        registerService("org.kie.api.io.KieResources", "org.drools.core.io.impl.ResourceFactoryServiceImpl", true);
        registerService("org.kie.api.concurrent.KieExecutors", "org.drools.core.concurrent.ExecutorProviderImpl", true);
        registerService("org.kie.api.KieServices", "org.drools.compiler.kie.builder.impl.KieServicesImpl", false);
        registerService("org.kie.internal.builder.KnowledgeBuilderFactoryService", "org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl", false);
        registerService("org.kie.api.internal.assembler.KieAssemblers", "org.drools.wiring.statics.StaticKieAssemblers", true);
        registerService("org.kie.api.internal.runtime.KieRuntimes", "org.kie.internal.services.KieRuntimesImpl", true);
        registerService("org.kie.api.internal.weaver.KieWeavers", "org.kie.internal.services.KieWeaversImpl", true);

        registerService("org.drools.compiler.kie.builder.impl.InternalKieModuleProvider", "org.drools.modelcompiler.CanonicalKieModuleProvider", false);
        registerService("org.drools.compiler.compiler.DecisionTableProvider", "org.drools.decisiontable.DecisionTableProviderImpl", false);
        registerService("org.drools.compiler.kie.builder.impl.KieBaseUpdaters", "org.drools.compiler.kie.builder.impl.KieBaseUpdatersImpl", false);

        registerService("org.drools.core.reteoo.RuntimeComponentFactory", "org.drools.kiesession.factory.RuntimeComponentFactoryImpl", false);

        // tms
        registerService("org.drools.core.common.TruthMaintenanceSystemFactory", "org.drools.tms.TruthMaintenanceSystemFactoryImpl", false);
        registerService("org.drools.kiesession.factory.KnowledgeHelperFactory", "org.drools.tms.TruthMaintenanceSystemKnowledgeHelperFactoryImpl", false);
        registerService("org.drools.core.reteoo.AgendaComponentFactory", "org.drools.tms.TruthMaintenanceSystemAgendaComponentFactory", false);

        registerService("org.drools.core.marshalling.impl.ProcessMarshallerFactoryService", "org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl", false);
        registerService("org.drools.core.runtime.process.ProcessRuntimeFactoryService", "org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl", false);

        registerService("org.drools.core.base.XMLSupport", "org.drools.xml.support.XMLSupportImpl", false);

        constructorMap.put("TimerService", SimpleInstanceCreator.constructor("org.drools.core.time.impl.JDKTimerService"));

        // pmml
        registerKieRuntimeService("org.kie.pmml.api.runtime.PMMLRuntime", "org.kie.pmml.evaluator.core.service.PMMLRuntimeService", false);
        registerKieWeaverService("org.kie.pmml.evaluator.assembler.PMMLWeaverService", false);

        // dmn
        registerKieRuntimeService("org.kie.dmn.api.core.DMNRuntime", "org.kie.dmn.core.runtime.DMNRuntimeService", false);
        registerKieWeaverService("org.kie.dmn.core.weaver.DMNWeaverService", false);

        // marshalling
        registerService(org.kie.api.marshalling.KieMarshallers.class.getCanonicalName(), "org.drools.serialization.protobuf.MarshallerProviderImpl", false);
        registerService("org.drools.compiler.kie.builder.impl.CompilationCacheProvider", "org.drools.serialization.protobuf.CompilationCacheProviderImpl", false);
    }

    protected void registerService(String service, String implementation, boolean mandatory) {
        try {
            serviceMap.put(Class.forName(service), SimpleInstanceCreator.instance(implementation));
        } catch (Exception e) {
            commonManageException(service, e, mandatory);
        }
    }

    private void registerKieRuntimeService(String runtimeName, String kieRuntimeServiceImplementation, boolean mandatory) {
        try {
            KieRuntimeService kieRuntimeService = (KieRuntimeService) SimpleInstanceCreator.instance(kieRuntimeServiceImplementation);
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

    private void commonManageException(String ignoredServiceType, Exception e, boolean mandatory) {
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
    public <T> List<T> getAll(Class<T> cls) {
        return Collections.singletonList(get(cls));
    }

    public <T> T newInstance(String name) {
        try {
            return (T) constructorMap.get(name).newInstance();
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}
