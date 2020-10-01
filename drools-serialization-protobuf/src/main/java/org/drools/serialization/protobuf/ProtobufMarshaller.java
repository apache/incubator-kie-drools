/*
 * Copyright 2010, 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.serialization.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.InternalMarshaller;
import org.drools.core.marshalling.impl.KieSessionInitializer;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.drools.serialization.protobuf.timers.BehaviorJobContextTimerInputMarshaller;
import org.drools.serialization.protobuf.timers.ExpireJobContextTimerInputMarshaller;
import org.drools.serialization.protobuf.timers.TimerNodeTimerInputMarshaller;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.marshalling.MarshallingConfiguration;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * A Marshaller implementation that uses ProtoBuf as the marshalling
 * framework in order to support backward compatibility with
 * marshalled sessions
 * 
 */
public class ProtobufMarshaller
        implements
        InternalMarshaller {

    protected KieSessionInitializer initializer;

    public KieSessionInitializer getInitializer() {
        return initializer;
    }

    public void setInitializer( KieSessionInitializer initializer ) {
        this.initializer = initializer;
    }

    public static final Map<Integer, TimersInputMarshaller> TIMER_READERS = new HashMap<>();
    static {
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.BEHAVIOR_VALUE, new BehaviorJobContextTimerInputMarshaller() );
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.EXPIRE_VALUE, new ExpireJobContextTimerInputMarshaller() );
        TIMER_READERS.put( ProtobufMessages.Timers.TimerType.TIMER_NODE_VALUE, new TimerNodeTimerInputMarshaller() );
    }

    protected KieBase                             kbase;
    protected RuleBaseConfiguration               ruleBaseConfig;
    protected MarshallingConfiguration            marshallingConfig;
    protected ObjectMarshallingStrategyStore      strategyStore;

    public ProtobufMarshaller(KieBase kbase,
                              MarshallingConfiguration marshallingConfig) {
        this.kbase = kbase;
        this.ruleBaseConfig = RuleBaseConfiguration.getDefaultInstance();
        this.marshallingConfig = marshallingConfig;
        this.strategyStore = this.marshallingConfig.getObjectMarshallingStrategyStore();
    }

    public StatefulKnowledgeSession unmarshall(final InputStream stream) throws IOException,
                                                                        ClassNotFoundException {
        return unmarshall( stream, null, null );
    }

    public StatefulKnowledgeSession unmarshall(final InputStream stream,
                                               KieSessionConfiguration config,
                                               Environment environment) throws IOException, ClassNotFoundException {
        return unmarshallWithMessage(stream, config, environment).getSession();
    }

    public void unmarshall(final InputStream stream,
                           final KieSession ksession) throws IOException, ClassNotFoundException {
        ProtobufMarshallerReaderContext context = getMarshallerReaderContext(stream, ksession.getEnvironment());
        ProtobufInputMarshaller.readSession((StatefulKnowledgeSessionImpl) ksession, context);
        context.close();
    }

    public void marshall(final OutputStream stream,
                         final KieSession ksession) throws IOException {
        marshall( stream, ksession, ksession.getSessionClock().getCurrentTime() );
    }

    public void marshall(final OutputStream stream,
                         final KieSession ksession,
                         final long clockTime) throws IOException {
        ((InternalWorkingMemory) ksession).flushPropagations();
        ProtobufMarshallerWriteContext context = new ProtobufMarshallerWriteContext( stream,
                                                                     (InternalKnowledgeBase) kbase,
                                                                     (InternalWorkingMemory) ksession,
                                                                     RuleBaseNodes.getNodeMap( (InternalKnowledgeBase) kbase ),
                                                                     this.strategyStore,
                                                                     this.marshallingConfig.isMarshallProcessInstances(),
                                                                     this.marshallingConfig.isMarshallWorkItems(),
                                                                     ksession.getEnvironment() );
        context.setClockTime( clockTime );
        ProtobufOutputMarshaller.writeSession( context );
        context.close();
    }

    public MarshallingConfiguration getMarshallingConfiguration() {
        return marshallingConfig;
    }

    public ReadSessionResult unmarshallWithMessage( final InputStream stream,
                                                    KieSessionConfiguration config,
                                                    Environment environment) throws IOException, ClassNotFoundException {
        if ( config == null ) {
            config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }

        if ( environment == null ) {
            environment = KieServices.get().newEnvironment();
        }

        ProtobufMarshallerReaderContext context = getMarshallerReaderContext(stream, environment);
        int id = ((KnowledgeBaseImpl) this.kbase).nextWorkingMemoryCounter();
        ReadSessionResult readSessionResult = ProtobufInputMarshaller.readSession(context,
                                                                                  id,
                                                                                  environment,
                                                                                  (SessionConfiguration) config,
                                                                                  initializer);
        context.close();
        if ( ((SessionConfiguration) config).isKeepReference() ) {
            ((KnowledgeBaseImpl) this.kbase).addStatefulSession(readSessionResult.getSession());
        }
        return readSessionResult;
    }

    private ProtobufMarshallerReaderContext getMarshallerReaderContext( final InputStream inputStream, final Environment environment) throws IOException {
        return new ProtobufMarshallerReaderContext(inputStream,
                                           (KnowledgeBaseImpl) kbase,
                                           RuleBaseNodes.getNodeMap((KnowledgeBaseImpl) kbase),
                                           this.strategyStore,
                                           TIMER_READERS,
                                           this.marshallingConfig.isMarshallProcessInstances(),
                                           this.marshallingConfig.isMarshallWorkItems(),
                                           environment);
    }

}
