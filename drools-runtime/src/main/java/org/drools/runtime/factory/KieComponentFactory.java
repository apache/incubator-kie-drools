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

package org.drools.runtime.factory;

import java.io.Serializable;

import org.drools.core.SessionConfiguration;
import org.drools.core.base.FieldDataFactory;
import org.drools.core.base.FieldFactory;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.BeliefSystemFactory;
import org.drools.runtime.agenda.DefaultAgendaFactory;
import org.drools.core.common.DefaultNamedEntryPointFactory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPointFactory;
import org.drools.core.common.PhreakBeliefSystemFactory;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.runtime.consequence.DefaultKnowledgeHelper;
import org.drools.runtime.session.KieSessionsPoolImpl;
import org.drools.runtime.session.StatefulKnowledgeSessionImpl;
import org.drools.runtime.session.StatelessKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public class KieComponentFactory implements Serializable, RuntimeComponentFactory {

    public static final KieComponentFactory DEFAULT = new KieComponentFactory();

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

    private PropagationContextFactory propagationFactory = PhreakPropagationContextFactory.getInstance();

    
    public PropagationContextFactory getPropagationContextFactory() {
        return propagationFactory;
    }

    private BeliefSystemFactory bsFactory = new PhreakBeliefSystemFactory();

    
    public BeliefSystemFactory getBeliefSystemFactory() {
        return bsFactory;
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

    public TraitFactory getTraitFactory(InternalKnowledgeBase knowledgeBase) {
        return null;
    }

    public TraitRegistry getTraitRegistry(InternalKnowledgeBase knowledgeBase) {
        return null;
    }

    public ClassBuilderFactory getClassBuilderFactory() {
        return ClassBuilderFactory.get();
    }

    public KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator) {
        return new DefaultKnowledgeHelper( reteEvaluator );
    }

    public InternalWorkingMemory createStatefulSession(KnowledgeBaseImpl kbase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool) {
        if (fromPool || kbase.getSessionPool() == null) {
            StatefulKnowledgeSessionImpl session = ( StatefulKnowledgeSessionImpl ) RuntimeComponentFactory.get().getWorkingMemoryFactory()
                    .createWorkingMemory( kbase.nextWorkingMemoryCounter(), kbase, sessionConfig, environment );
            return internalInitSession( kbase, sessionConfig, session );
        }
        return (InternalWorkingMemory) kbase.getSessionPool().newKieSession( sessionConfig );
    }

    private StatefulKnowledgeSessionImpl internalInitSession( KnowledgeBaseImpl kbase, SessionConfiguration sessionConfig, StatefulKnowledgeSessionImpl session ) {
        if ( sessionConfig.isKeepReference() ) {
            kbase.addStatefulSession(session);
        }
        return session;
    }

    public StatelessKieSession createStatelessSession(KnowledgeBaseImpl kbase, KieSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl( kbase, conf );
    }

    public KieSessionsPool createSessionsPool(KnowledgeBaseImpl kBase, int initialSize) {
        return new KieSessionsPoolImpl(kBase, initialSize);
    }
}
