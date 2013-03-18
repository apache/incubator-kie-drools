/*
 * Copyright 2010, 2011 JBoss Inc
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

package org.drools.core.marshalling.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Scheduler.ActivationTimerInputMarshaller;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContextTimerInputMarshaller;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.reteoo.ReteooStatefulSession;
import org.drools.core.rule.SlidingTimeWindow.BehaviorJobContextTimerInputMarshaller;
import org.drools.core.spi.GlobalResolver;
import org.kie.api.KieBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.marshalling.Marshaller;
import org.kie.marshalling.MarshallingConfiguration;
import org.kie.marshalling.ObjectMarshallingStrategyStore;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.time.SessionClock;

public class DefaultMarshaller
        implements
        Marshaller {
    KieBase                     kbase;
    GlobalResolver                    globalResolver;
    RuleBaseConfiguration             ruleBaseConfig;
    MarshallingConfiguration          marshallingConfig;
    ObjectMarshallingStrategyStore    strategyStore;
    Map<Integer, TimersInputMarshaller> timerReaders;

    public DefaultMarshaller(KieBase kbase,
                             MarshallingConfiguration marshallingConfig) {
        this.kbase = kbase;
        this.ruleBaseConfig = (ruleBaseConfig != null) ? ruleBaseConfig : RuleBaseConfiguration.getDefaultInstance();
        this.marshallingConfig = marshallingConfig;
        this.strategyStore = this.marshallingConfig.getObjectMarshallingStrategyStore();

        this.timerReaders = new HashMap<Integer, TimersInputMarshaller>();
        this.timerReaders.put( (int) PersisterEnums.BEHAVIOR_TIMER, new BehaviorJobContextTimerInputMarshaller() );
        this.timerReaders.put( (int) PersisterEnums.ACTIVATION_TIMER, new ActivationTimerInputMarshaller() );
        this.timerReaders.put( (int) PersisterEnums.EXPIRE_TIMER, new ExpireJobContextTimerInputMarshaller() );
    }

    public StatefulKnowledgeSession unmarshall(final InputStream stream) throws IOException,
                                                                        ClassNotFoundException {
        return unmarshall( stream, null, null );
    }

    /* (non-Javadoc)
     * @see org.kie.marshalling.Marshaller#read(java.io.InputStream, org.kie.common.InternalRuleBase, int, org.kie.concurrent.ExecutorService)
     */
    public StatefulKnowledgeSession unmarshall(final InputStream stream,
                                               KieSessionConfiguration config,
                                               Environment environment) throws IOException,
                                                                       ClassNotFoundException {
        if ( config == null ) {
            config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }

        if ( environment == null ) {
            environment = KnowledgeBaseFactory.newEnvironment();
        }

        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase,
                                                                       RuleBaseNodes.getNodeMap( (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase ),
                                                                       this.strategyStore,
                                                                       this.timerReaders,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       this.marshallingConfig.isMarshallWorkItems(),
                                                                       environment );

        int id = ((ReteooRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).nextWorkingMemoryCounter();
        RuleBaseConfiguration conf = ((ReteooRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).getConfiguration();

        ReteooStatefulSession session = InputMarshaller.readSession( context,
                                                                     id,
                                                                     environment,
                                                                     (SessionConfiguration) config );
        context.close();
        if ( ((SessionConfiguration) config).isKeepReference() ) {
            ((ReteooRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).addStatefulSession( session );
        }
        return (StatefulKnowledgeSession) session.getKnowledgeRuntime();

    }

    public void unmarshall(final InputStream stream,
                           final KieSession ksession) throws IOException,
                                                                   ClassNotFoundException {
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase,
                                                                       RuleBaseNodes.getNodeMap( (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase ),
                                                                       this.strategyStore,
                                                                       this.timerReaders,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       marshallingConfig.isMarshallWorkItems(),
                                                                       ksession.getEnvironment() );

        InputMarshaller.readSession( (ReteooStatefulSession) ((StatefulKnowledgeSessionImpl) ksession).session,
                                     context );
        context.close();

    }

    public void marshall(final OutputStream stream,
                         final KieSession ksession) throws IOException {
        marshall( stream, ksession, ksession.<SessionClock> getSessionClock().getCurrentTime() );
    }

    /* (non-Javadoc)
     * @see org.kie.marshalling.Marshaller#write(java.io.OutputStream, org.kie.common.InternalRuleBase, org.kie.StatefulSession)
     */
    public void marshall(final OutputStream stream,
                         final KieSession ksession,
                         final long clockTime) throws IOException {
        MarshallerWriteContext context = new MarshallerWriteContext( stream,
                                                                     (InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase(),
                                                                     (InternalWorkingMemory) ((StatefulKnowledgeSessionImpl) ksession).session,
                                                                     RuleBaseNodes.getNodeMap( (InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase() ),
                                                                     this.strategyStore,
                                                                     this.marshallingConfig.isMarshallProcessInstances(),
                                                                     this.marshallingConfig.isMarshallWorkItems(),
                                                                     ksession.getEnvironment() );
        context.clockTime = clockTime;
        OutputMarshaller.writeSession( context );
        context.close();
    }

    public MarshallingConfiguration getMarshallingConfiguration() {
        return marshallingConfig;
    }

}
