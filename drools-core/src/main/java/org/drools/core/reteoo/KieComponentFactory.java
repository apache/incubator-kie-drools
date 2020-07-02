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

import org.drools.core.base.FieldDataFactory;
import org.drools.core.base.FieldFactory;
import org.drools.core.base.TraitDisabledHelper;
import org.drools.core.base.TraitHelper;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.BeliefSystemFactory;
import org.drools.core.common.DefaultAgendaFactory;
import org.drools.core.common.DefaultNamedEntryPointFactory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.NamedEntryPointFactory;
import org.drools.core.common.PhreakBeliefSystemFactory;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PhreakWorkingMemoryFactory;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.DefaultClassBuilderFactory;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.drools.core.rule.DefaultLogicTransformerFactory;
import org.drools.core.rule.LogicTransformerFactory;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleFactoryImpl;
import org.drools.core.util.TripleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieComponentFactory implements Serializable {

    Logger logger = LoggerFactory.getLogger(KieComponentFactory.class);

    public static final KieComponentFactory DEFAULT = new KieComponentFactory();

    public static KieComponentFactory createKieComponentFactory() {
        Optional<KieComponentFactoryFactory> kieComponentFactory = ServiceRegistryUtils.optionalService(KieComponentFactoryFactory.class);
        return kieComponentFactory.map(KieComponentFactoryFactory::createKieComponentFactory).orElseGet(KieComponentFactory::new);
    }

    private FactHandleFactory handleFactory = new ReteooFactHandleFactory();

    
    public FactHandleFactory getFactHandleFactoryService() {
        return handleFactory;
    }

    
    public NamedEntryPointFactory getNamedEntryPointFactory() {
        return new DefaultNamedEntryPointFactory();
    }

    private WorkingMemoryFactory wmFactory = PhreakWorkingMemoryFactory.getInstance();

    
    public WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }

    private NodeFactory nodeFactory = PhreakNodeFactory.getInstance();

    
    public NodeFactory getNodeFactoryService() {
        return nodeFactory;
    }

    
    public void setNodeFactoryProvider(NodeFactory provider) {
        nodeFactory = provider;
    }

    private PropagationContextFactory propagationFactory = PhreakPropagationContextFactory.getInstance();

    
    public PropagationContextFactory getPropagationContextFactory() {
        return propagationFactory;
    }

    private BeliefSystemFactory bsFactory = new PhreakBeliefSystemFactory();

    
    public BeliefSystemFactory getBeliefSystemFactory() {
        return bsFactory;
    }

    private RuleBuilderFactory ruleBuilderFactory = new ReteooRuleBuilderFactory();

    
    public RuleBuilderFactory getRuleBuilderFactory() {
        return ruleBuilderFactory;
    }

    private AgendaFactory agendaFactory = DefaultAgendaFactory.getInstance();

    
    public AgendaFactory getAgendaFactory() {
        return agendaFactory;
    }

    private AgendaGroupFactory agendaGroupFactory = PriorityQueueAgendaGroupFactory.getInstance();

    
    public AgendaGroupFactory getAgendaGroupFactory() {
        return agendaGroupFactory;
    }

    private FieldDataFactory fieldFactory = FieldFactory.getInstance();

    
    public FieldDataFactory getFieldFactory() {
        return fieldFactory;
    }

    private TripleFactory tripleFactory = new TripleFactoryImpl();

    
    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    private LogicTransformerFactory logicTransformerFactory = new DefaultLogicTransformerFactory();

    
    public LogicTransformerFactory getLogicTransformerFactory() {
        return logicTransformerFactory;
    }

    
    public TraitFactory initTraitFactory(InternalKnowledgeBase knowledgeBase) {
        return null;
    }

    
    public TraitFactory getTraitFactory() {
        return null;
    }

    
    public TraitRegistry getTraitRegistry() {
        return null;
    }

    private TripleStore tripleStore = new TripleStore();

    
    public TripleStore getTripleStore() {
        return tripleStore;
    }

    
    public TraitHelper createTraitHelper(InternalWorkingMemoryActions workingMemory, InternalWorkingMemoryEntryPoint nep) {
        return new TraitDisabledHelper();
    }

    private ClassBuilderFactory classBuilderFactory = new DefaultClassBuilderFactory();

    
    public ClassBuilderFactory getClassBuilderFactory() {
        return classBuilderFactory;
    }

    
    public Class<?> getBaseTraitProxyClass() {
        return null;
    }

    
    public InternalKnowledgePackage createKnowledgePackage(String name) {
        return new KnowledgePackageImpl(name);
    }
}
