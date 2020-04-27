/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

import org.drools.core.base.FieldDataFactory;
import org.drools.core.base.FieldFactory;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.BeliefSystemFactory;
import org.drools.core.common.DefaultAgendaFactory;
import org.drools.core.common.PhreakBeliefSystemFactory;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PhreakWorkingMemoryFactory;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.WorkingMemoryFactory;
//import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.traits.TraitCoreService;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.drools.core.rule.DefaultLogicTransformerFactory;
import org.drools.core.rule.LogicTransformerFactory;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleFactoryImpl;
import org.drools.core.util.TripleStore;
import org.kie.api.internal.utils.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieComponentFactory implements Serializable {

    Logger logger = LoggerFactory.getLogger(KieComponentFactory.class);

    public static final KieComponentFactory DEFAULT = new KieComponentFactory();

    public static KieComponentFactory getDefault() {
        return DEFAULT;
    }

    private FactHandleFactory handleFactory = new ReteooFactHandleFactory();


    public FactHandleFactory getFactHandleFactoryService() {
         return handleFactory;
    }

    public void setHandleFactoryProvider( FactHandleFactory provider ) {
        handleFactory = provider;
    }

    public void setDefaultHandleFactoryProvider() {
        handleFactory = new ReteooFactHandleFactory();
    }

    private WorkingMemoryFactory wmFactory = PhreakWorkingMemoryFactory.getInstance();

    public WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }

    public void setWorkingMemoryFactory(WorkingMemoryFactory wmFactory) {
        this.wmFactory = wmFactory;
    }

    private NodeFactory nodeFactory = PhreakNodeFactory.getInstance();

    public NodeFactory getNodeFactoryService() {
        return nodeFactory;
    }

    public void setNodeFactoryProvider(NodeFactory provider) {
        nodeFactory = provider;
    }

    public void setDefaultNodeFactoryProvider() {
        nodeFactory = PhreakNodeFactory.getInstance();
    }

    public static NodeFactory getDefaultNodeFactoryProvider() {
        return PhreakNodeFactory.getInstance();
    }

    private PropagationContextFactory propagationFactory = PhreakPropagationContextFactory.getInstance();

    public void setPropagationContextFactory(PropagationContextFactory factory) {
        propagationFactory = factory;
    }

    public PropagationContextFactory getPropagationContextFactory() {
        return propagationFactory;
    }


    private BeliefSystemFactory bsFactory = new PhreakBeliefSystemFactory();

    public void setPropagationContextFactory(BeliefSystemFactory factory) {
        bsFactory = factory;
    }

    public BeliefSystemFactory getBeliefSystemFactory() {
        return bsFactory;
    }


    private RuleBuilderFactory ruleBuilderFactory = new ReteooRuleBuilderFactory();

    public RuleBuilderFactory getRuleBuilderFactory() {
        return ruleBuilderFactory;
    }

    public void setRuleBuilderProvider(RuleBuilderFactory provider) {
        ruleBuilderFactory = provider;
    }

    public void setDefaultRuleBuilderProvider() {
        ruleBuilderFactory = new ReteooRuleBuilderFactory();
    }

    public static RuleBuilderFactory getDefaultRuleBuilderFactory() {
        return new ReteooRuleBuilderFactory();
    }


    private AgendaFactory agendaFactory = DefaultAgendaFactory.getInstance();

    public AgendaFactory getAgendaFactory() {
        return agendaFactory;
    }

    public void setAgendaFactory(AgendaFactory provider) {
        agendaFactory = provider;
    }

    public void setDefaultAgendaFactory() {
        agendaFactory = DefaultAgendaFactory.getInstance();
    }

    public static AgendaFactory getDefaultAgendaFactory() {
        return DefaultAgendaFactory.getInstance();
    }


    private AgendaGroupFactory agendaGroupFactory = PriorityQueueAgendaGroupFactory.getInstance();

    public AgendaGroupFactory getAgendaGroupFactory() {
        return agendaGroupFactory;
    }

    public void setAgendaGroupFactory(AgendaGroupFactory provider) {
        agendaGroupFactory = provider;
    }

    public void setDefaultAgendaGroupFactory() {
        agendaFactory = DefaultAgendaFactory.getInstance();
    }

    public static AgendaGroupFactory getDefaultAgendaGroupFactory() {
        return PriorityQueueAgendaGroupFactory.getInstance();
    }


    private FieldDataFactory fieldFactory = FieldFactory.getInstance();

    public FieldDataFactory getFieldFactory() {
        return fieldFactory;
    }

    public void setFieldDataFactory(FieldDataFactory provider) {
        fieldFactory = provider;
    }

    public void setDefaultFieldFactory() {
        fieldFactory = FieldFactory.getInstance();
    }

    public static FieldDataFactory getDefaultFieldFactory() {
        return FieldFactory.getInstance();
    }


    private TripleFactory tripleFactory = new TripleFactoryImpl();

    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    public void setTripleFactory(TripleFactory provider) {
        tripleFactory = provider;
    }

    public void setDefaultTripleFactory() {
        tripleFactory = new TripleFactoryImpl();
    }

    public static TripleFactory getDefaultTripleFactory() {
        return new TripleFactoryImpl();
    }

    private LogicTransformerFactory logicTransformerFactory = new DefaultLogicTransformerFactory();

    public LogicTransformerFactory getLogicTransformerFactory() {
        return logicTransformerFactory;
    }

    public void setLogicTransformerFactory(LogicTransformerFactory provider) {
        logicTransformerFactory = provider;
    }

    public void setDefaultLogicTransformerFactory() {
        logicTransformerFactory = new DefaultLogicTransformerFactory();
    }

    public static LogicTransformerFactory getDefaultLogicTransformerFactory() {
        return new DefaultLogicTransformerFactory();
    }

    private TraitFactory traitFactory;

    public TraitFactory getTraitFactory() {
        if(traitFactory == null) {
            traitFactory =
                    fromTraitRegistry(TraitCoreService::createTraitFactory)
                    .orElse(null);
        }
        return traitFactory;
    }

    public void setTraitFactory( TraitFactory tf ) {
        traitFactory = tf;
    }

    public static <T> Optional<T> fromTraitRegistry(Function<TraitCoreService, T> producer) {
        try {
            return getTraitCoreService()
                    .map(producer);
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    private static Optional<TraitCoreService> getTraitCoreService() {
        return Optional.ofNullable(ServiceRegistry.getInstance().get(TraitCoreService.class));
    }

    private TraitRegistry traitRegistry;

    public TraitRegistry getTraitRegistry() {
        if ( traitRegistry == null ) {
            traitRegistry =
                    fromTraitRegistry(TraitCoreService::createRegistry)
                    .orElse(null);
        }
        return traitRegistry;
    }

    public void setTraitRegistry(TraitRegistry traitRegistry) {
        this.traitRegistry = traitRegistry;
    }

    private TripleStore tripleStore = new TripleStore();

    public TripleStore getTripleStore() {
        return tripleStore;
    }

    public void setTraitFactory( TripleStore tr ) {
        tripleStore = tr;
    }

    public void setDefaultTripleStore() {
        tripleStore = new TripleStore();
    }

    public static TripleStore getDefaultTripleStore() {
        return new TripleStore();
    }

    private ClassBuilderFactory classBuilderFactory = new ClassBuilderFactory();

    public ClassBuilderFactory getClassBuilderFactory() {
        return classBuilderFactory;
    }

    public void setClassBuilderFactory( ClassBuilderFactory tf ) {
        classBuilderFactory = tf;
    }

    private Class<?> baseTraitProxyClass;

    public Class<?> getBaseTraitProxyClass() {
        if(baseTraitProxyClass == null) {
            baseTraitProxyClass = fromTraitRegistry(TraitCoreService::baseTraitProxyClass)
                    .orElseThrow(RuntimeException::new);
        }
        return baseTraitProxyClass;
    }

    public void setBaseTraitProxyClass(Class<?> baseTraitProxyClass) {
        this.baseTraitProxyClass = baseTraitProxyClass;
    }
}
