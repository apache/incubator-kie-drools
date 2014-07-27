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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Scheduler.ActivationTimerInputMarshaller;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeTimerInputMarshaller;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContextTimerInputMarshaller;
import org.drools.core.rule.SlidingTimeWindow.BehaviorJobContextTimerInputMarshaller;
import org.drools.core.spi.GlobalResolver;
import org.kie.api.KieBase;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.MarshallingConfiguration;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A Marshaller implementation that uses ProtoBuf as the marshalling
 * framework in order to support backward compatibility with
 * marshalled sessions
 * 
 * @author etirelli
 */
public class ProtobufMarshaller
        implements
        Marshaller {
    
    public static final Map<Integer, TimersInputMarshaller> TIMER_READERS = new HashMap<Integer, TimersInputMarshaller>();
    static {
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.BEHAVIOR_VALUE, new BehaviorJobContextTimerInputMarshaller() );
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.ACTIVATION_VALUE, new ActivationTimerInputMarshaller() );
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.EXPIRE_VALUE, new ExpireJobContextTimerInputMarshaller() );
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.TIMER_NODE_VALUE, new TimerNodeTimerInputMarshaller() );
    }
    
    KieBase                             kbase;
    RuleBaseConfiguration               ruleBaseConfig;
    MarshallingConfiguration            marshallingConfig;
    ObjectMarshallingStrategyStore      strategyStore;

    public ProtobufMarshaller(KieBase kbase,
                              MarshallingConfiguration marshallingConfig) {
        this.kbase = kbase;
        this.ruleBaseConfig = (ruleBaseConfig != null) ? ruleBaseConfig : RuleBaseConfiguration.getDefaultInstance();
        this.marshallingConfig = marshallingConfig;
        this.strategyStore = this.marshallingConfig.getObjectMarshallingStrategyStore();
    }

    public StatefulKnowledgeSession unmarshall(final InputStream stream) throws IOException,
                                                                        ClassNotFoundException {
        return unmarshall( stream, null, null );
    }

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
                                                                       (KnowledgeBaseImpl) kbase,
                                                                       RuleBaseNodes.getNodeMap( (KnowledgeBaseImpl) kbase ),
                                                                       this.strategyStore,
                                                                       TIMER_READERS,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       this.marshallingConfig.isMarshallWorkItems(),
                                                                       environment );

        int id = ((KnowledgeBaseImpl) this.kbase).nextWorkingMemoryCounter();
        RuleBaseConfiguration conf = ((KnowledgeBaseImpl) this.kbase).getConfiguration();

        StatefulKnowledgeSessionImpl session = ProtobufInputMarshaller.readSession( context,
                                                                             id,
                                                                             environment,
                                                                             (SessionConfiguration) config );
        context.close();
        if ( ((SessionConfiguration) config).isKeepReference() ) {
            ((KnowledgeBaseImpl) this.kbase).addStatefulSession(session);
        }
        return session;

    }

    public void unmarshall(final InputStream stream,
                           final KieSession ksession) throws IOException,
                                                                   ClassNotFoundException {
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       (KnowledgeBaseImpl) kbase,
                                                                       RuleBaseNodes.getNodeMap( (KnowledgeBaseImpl) kbase ),
                                                                       this.strategyStore,
                                                                       TIMER_READERS,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       marshallingConfig.isMarshallWorkItems(),
                                                                       ksession.getEnvironment() );

        ProtobufInputMarshaller.readSession( (StatefulKnowledgeSessionImpl) ksession,
                                             context );
        context.close();

    }

    public void marshall(final OutputStream stream,
                         final KieSession ksession) throws IOException {
        marshall( stream, ksession, ksession.<SessionClock> getSessionClock().getCurrentTime() );
    }

    public void marshall(final OutputStream stream,
                         final KieSession ksession,
                         final long clockTime) throws IOException {
        MarshallerWriteContext context = new MarshallerWriteContext( stream,
                                                                     (InternalKnowledgeBase) kbase,
                                                                     (InternalWorkingMemory) ksession,
                                                                     RuleBaseNodes.getNodeMap( (InternalKnowledgeBase) kbase ),
                                                                     this.strategyStore,
                                                                     this.marshallingConfig.isMarshallProcessInstances(),
                                                                     this.marshallingConfig.isMarshallWorkItems(),
                                                                     ksession.getEnvironment() );
        context.clockTime = clockTime;
        ProtobufOutputMarshaller.writeSession( context );
        context.close();
    }

    public MarshallingConfiguration getMarshallingConfiguration() {
        return marshallingConfig;
    }

}
