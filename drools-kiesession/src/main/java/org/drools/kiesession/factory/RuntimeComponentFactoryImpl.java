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

package org.drools.kiesession.factory;

import java.io.Serializable;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.impl.RuleBase;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.kiesession.agenda.DefaultAgendaFactory;
import org.drools.kiesession.entrypoints.NamedEntryPointFactory;
import org.drools.kiesession.management.KieSessionMonitoringImpl;
import org.drools.kiesession.management.StatelessKieSessionMonitoringImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.KieSessionsPoolImpl;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.kiesession.session.StatelessKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public class RuntimeComponentFactoryImpl implements Serializable, RuntimeComponentFactory {

    public static final RuntimeComponentFactoryImpl DEFAULT = new RuntimeComponentFactoryImpl();

    private final FactHandleFactory handleFactory = new ReteooFactHandleFactory();
    private final PropagationContextFactory propagationFactory = PhreakPropagationContextFactory.getInstance();
    private final WorkingMemoryFactory wmFactory = PhreakWorkingMemoryFactory.getInstance();
    private final AgendaFactory agendaFactory = DefaultAgendaFactory.getInstance();
    private final AgendaGroupFactory agendaGroupFactory = PriorityQueueAgendaGroupFactory.getInstance();

    public FactHandleFactory getFactHandleFactoryService() {
        return handleFactory;
    }

    public EntryPointFactory getEntryPointFactory() {
        return new NamedEntryPointFactory();
    }

    public PropagationContextFactory getPropagationContextFactory() {
        return propagationFactory;
    }

    public AgendaFactory getAgendaFactory() {
        return agendaFactory;
    }

    public AgendaGroupFactory getAgendaGroupFactory() {
        return agendaGroupFactory;
    }

    public TraitFactory getTraitFactory(RuleBase knowledgeBase) {
        return null;
    }

    public final KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator) {
        return KnowledgeHelperFactory.get().createKnowledgeHelper(reteEvaluator);
    }

    public InternalWorkingMemory createStatefulSession(RuleBase ruleBase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) ruleBase;
        if (fromPool || kbase.getSessionPool() == null) {
            StatefulKnowledgeSessionImpl session = ( StatefulKnowledgeSessionImpl ) getWorkingMemoryFactory()
                    .createWorkingMemory( kbase.nextWorkingMemoryCounter(), kbase, sessionConfig, environment );
            return internalInitSession( kbase, sessionConfig, session );
        }
        return (InternalWorkingMemory) kbase.getSessionPool().newKieSession( sessionConfig );
    }

    private StatefulKnowledgeSessionImpl internalInitSession( InternalKnowledgeBase kbase, SessionConfiguration sessionConfig, StatefulKnowledgeSessionImpl session ) {
        if ( sessionConfig.isKeepReference() ) {
            kbase.addStatefulSession(session);
        }
        return session;
    }

    public StatelessKieSession createStatelessSession(RuleBase ruleBase, KieSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl( (InternalKnowledgeBase) ruleBase, conf );
    }

    public KieSessionsPool createSessionsPool(RuleBase ruleBase, int initialSize) {
        return new KieSessionsPoolImpl((InternalKnowledgeBase) ruleBase, initialSize);
    }

    public KieSessionMonitoringImpl createStatefulSessionMonitor(DroolsManagementAgent.CBSKey cbsKey) {
        return new KieSessionMonitoringImpl( cbsKey.getKcontainerId(), cbsKey.getKbaseId(), cbsKey.getKsessionName() );
    }

    public StatelessKieSessionMonitoringImpl createStatelessSessionMonitor(DroolsManagementAgent.CBSKey cbsKey) {
        return new StatelessKieSessionMonitoringImpl( cbsKey.getKcontainerId(), cbsKey.getKbaseId(), cbsKey.getKsessionName() );
    }

    protected WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }
}
