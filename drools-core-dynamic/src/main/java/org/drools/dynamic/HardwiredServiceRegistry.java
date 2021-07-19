/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.dynamic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.internal.weaver.KieWeavers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardwiredServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(HardwiredServiceRegistry.class);

    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    private Map<String, Constructor<?>> constructorMap = new HashMap<>();

    protected HardwiredServiceRegistry() {
        wireServices();
    }

    protected void wireServices() {
        // drools-core
        registerService("org.kie.api.io.KieResources", "org.drools.core.io.impl.ResourceFactoryServiceImpl", true);
        registerService("org.kie.api.concurrent.KieExecutors", "org.drools.core.concurrent.ExecutorProviderImpl", true);

        // drools-compiler
        registerService("org.kie.api.KieServices", "org.drools.compiler.kie.builder.impl.KieServicesImpl", true);
        registerService("org.kie.internal.builder.KnowledgeBuilderFactoryService", "org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl", true);
        registerService("org.kie.internal.builder.JaxbConfigurationFactoryService", "org.drools.compiler.builder.impl.JaxbConfigurationFactoryServiceImpl", true);
        registerService("org.drools.compiler.kie.builder.impl.KieBaseUpdaters", "org.drools.compiler.kie.builder.impl.KieBaseUpdatersImpl", true);

        // drools-alphanetwork-compiler
        registerOverloadedService("org.drools.compiler.kie.builder.impl.KieBaseUpdaters", "org.drools.ancompiler.KieBaseUpdaterANCFactory", false);

        // drools-beliefs
        registerOverloadedService(KieAssemblers.class, "org.drools.beliefs.bayes.assembler.BayesAssemblerService", false);
        registerOverloadedService(KieRuntimes.class, "org.drools.beliefs.bayes.runtime.BayesRuntimeService", false);
        registerOverloadedService(KieWeavers.class, "org.drools.beliefs.bayes.weaver.BayesWeaverService", false);

        // drools-decisiontables
        registerService("org.drools.compiler.compiler.DecisionTableProvider", "org.drools.decisiontable.DecisionTableProviderImpl", false);

        // drools-metrics
        registerService("org.drools.core.phreak.PhreakNetworkNodeFactory", "org.drools.metric.phreak.MetricPhreakNetworkNodeFactoryImpl", false);
        registerService("org.drools.core.reteoo.builder.BetaNodeConstraintFactory", "org.drools.metric.reteoo.builder.MetricBetaNodeConstraintFactoryImpl", false);
        registerService("org.drools.core.rule.EvalConditionFactory", "org.drools.metric.rule.MetricEvalConditionFactoryImpl", false);

        // drools-model-compiler
        registerService("org.drools.compiler.kie.builder.impl.InternalKieModuleProvider", "org.drools.modelcompiler.CanonicalKieModuleProvider", false);
        registerOverloadedService(KieAssemblers.class, "org.drools.modelcompiler.drlx.DrlxAssembler", false);

        // drools-mvel
        registerService("org.drools.compiler.rule.builder.ConstraintBuilder", "org.drools.mvel.MVELConstraintBuilder", false);
        registerService("org.drools.core.base.CoreComponentsBuilder", "org.drools.mvel.MVELCoreComponentsBuilder", false);
        registerService("org.drools.core.base.FieldAccessorFactory", "org.drools.mvel.asm.ClassFieldAccessorFactory", false);
        registerService("org.drools.core.factmodel.ClassBuilderFactory", "org.drools.mvel.asm.DefaultClassBuilderFactory", false);

        // drools-persistence-jpa
        registerService("org.kie.api.persistence.jpa.KieStoreServices", "org.drools.persistence.jpa.KnowledgeStoreServiceImpl", false);
        registerService("org.kie.internal.process.CorrelationKeyFactory", "org.jbpm.persistence.correlation.JPACorrelationKeyFactory", false);

        // drools-ruleunit
        registerService("org.kie.internal.ruleunit.RuleUnitComponentFactory", "org.drools.ruleunit.impl.RuleUnitComponentFactoryImpl", false);

        // drools-scorecards
        registerService("org.drools.compiler.compiler.ScoreCardProvider", "org.drools.scorecards.ScoreCardProviderImpl", false);

        // drools-serialization-protobuf
        registerService("org.kie.api.marshalling.KieMarshallers", "org.drools.serialization.protobuf.MarshallerProviderImpl", false);
        registerService("org.drools.compiler.kie.builder.impl.CompilationCacheProvider", "org.drools.serialization.protobuf.CompilationCacheProviderImpl", false);

        // drools-traits
        registerService("org.drools.core.reteoo.KieComponentFactoryFactory", "org.drools.traits.core.reteoo.TraitKieComponentFactoryFactory", false);
        registerService("org.drools.compiler.builder.impl.TypeDeclarationBuilderFactory", "org.drools.traits.compiler.builder.impl.TraitTypeDeclarationBuilderFactory", false);
        registerService("org.kie.internal.process.CorrelationKeyFactory", "org.jbpm.persistence.correlation.JPACorrelationKeyFactory", false);

        // drools-workbench-models-guided-dtable
        registerService("org.drools.compiler.compiler.GuidedDecisionTableProvider", "org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableProviderImpl", false);

        //drools-workbench-models-guided-scorecard
        registerService("org.drools.compiler.compiler.GuidedScoreCardProvider", "org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardProviderImpl", false);

        // drools-workbench-models-guided-template
        registerService("org.drools.compiler.compiler.GuidedRuleTemplateProvider", "org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateProviderImpl", false);

        // kie-ci
        registerService("org.kie.api.builder.KieScannerFactoryService", "org.kie.scanner.KieScannerFactoryServiceImpl", false);
        registerService("org.kie.internal.utils.ClassLoaderResolver", "org.kie.scanner.MavenClassLoaderResolver", false);

        // kie-dmn-core
        registerOverloadedService(KieAssemblers.class, "org.kie.dmn.core.assembler.DMNAssemblerService", false);
        registerOverloadedService(KieRuntimes.class, "org.kie.dmn.core.runtime.DMNRuntimeService", false);
        registerOverloadedService(KieWeavers.class, "org.kie.dmn.core.weaver.DMNWeaverService", false);

        // kie-internal
        registerService("org.kie.api.internal.assembler.KieAssemblers", "org.kie.internal.services.KieAssemblersImpl", false);
        registerService("org.kie.api.internal.runtime.KieRuntimes", "org.kie.internal.services.KieRuntimesImpl", false);
        registerService("org.kie.api.internal.weaver.KieWeavers", "org.kie.internal.services.KieWeaversImpl", false);
        registerService("org.kie.api.internal.runtime.beliefs.KieBeliefs", "org.kie.internal.services.KieBeliefsImpl", false);

        // kie-pmml
        registerOverloadedService(KieAssemblers.class, "org.kie.pmml.assembler.PMMLAssemblerService", false);

        // kie-pmml-evalator-assembler
        registerOverloadedService(KieAssemblers.class, "org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService", false);
        registerOverloadedService(KieWeavers.class, "org.kie.pmml.evaluator.assembler.PMMLWeaverService", false);
        registerService("org.kie.internal.pmml.PMMLCommandExecutorFactory", "org.kie.pmml.evaluator.assembler.factories.PMMLCommandExecutorFactoryImpl", false);

        // kie-pmml-evalator-core
        registerOverloadedService(KieRuntimes.class, "org.kie.pmml.evaluator.core.service.PMMLRuntimeService", false);
    }

    protected boolean registerService(String service, String implementation, boolean mandatory) {
        try {
            serviceMap.put(Class.forName(service), instance(implementation));
            return true;
        } catch (Exception e) {
            commonManageException(service, e, mandatory);
        }
        return false;
    }

    protected void registerOverloadedService(String kieServiceClass, String implementation, boolean mandatory) {
        try {
            registerOverloadedService(Class.forName(kieServiceClass), implementation, mandatory);
        } catch (Exception e) {
            commonManageException("KieWeaverService", e, mandatory);
        }
    }

    protected void registerOverloadedService(Class<?> kieServiceClass, String implementation, boolean mandatory) {
        try {
            final Consumer kieService = (Consumer) serviceMap.get(kieServiceClass);
            Object kieServiceImpl = instance(implementation);
            kieService.accept(kieServiceImpl);
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

    protected static Constructor<?> constructor(String className) {
        try {
            return Class.forName(className).getConstructor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Object instance(String className) {
        try {
            return Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
