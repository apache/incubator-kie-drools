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

package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Scheduler.ActivationTimerInputMarshaller;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ObjectTypeNode.ExpireJobContextTimerInputMarshaller;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.rule.SlidingTimeWindow.BehaviorJobContextTimerInputMarshaller;
import org.drools.spi.GlobalResolver;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.marshalling.Marshaller;
import org.kie.marshalling.MarshallingConfiguration;
import org.kie.marshalling.ObjectMarshallingStrategyStore;
import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.time.SessionClock;

public class DefaultMarshaller
        implements
        Marshaller {
    KnowledgeBase                     kbase;
    GlobalResolver                    globalResolver;
    RuleBaseConfiguration             ruleBaseConfig;
    MarshallingConfiguration          marshallingConfig;
    ObjectMarshallingStrategyStore    strategyStore;
    Map<Integer, TimersInputMarshaller> timerReaders;

    public DefaultMarshaller(KnowledgeBase kbase,
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
                                               KnowledgeSessionConfiguration config,
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
                           final StatefulKnowledgeSession ksession) throws IOException,
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
                         final StatefulKnowledgeSession ksession) throws IOException {
        marshall( stream, ksession, ksession.<SessionClock> getSessionClock().getCurrentTime() );
    }

    /* (non-Javadoc)
     * @see org.kie.marshalling.Marshaller#write(java.io.OutputStream, org.kie.common.InternalRuleBase, org.kie.StatefulSession)
     */
    public void marshall(final OutputStream stream,
                         final StatefulKnowledgeSession ksession,
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
