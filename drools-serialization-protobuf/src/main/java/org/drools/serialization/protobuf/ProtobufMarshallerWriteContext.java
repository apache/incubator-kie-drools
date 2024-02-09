/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.serialization.protobuf;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.serialization.protobuf.marshalling.ObjectMarshallingStrategyStoreImpl;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeJobContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.serialization.protobuf.timers.BehaviorJobContextTimerOutputMarshaller;
import org.drools.serialization.protobuf.timers.ExpireJobContextTimerOutputMarshaller;
import org.drools.serialization.protobuf.timers.TimerNodeTimerOutputMarshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.marshalling.MarshallerFactory;

public class ProtobufMarshallerWriteContext extends ObjectOutputStream implements MarshallerWriteContext {
    private final InternalKnowledgeBase kBase;
    private final InternalWorkingMemory                                            wm;
    private final Map<Integer, BaseNode>                                           sinks;

    private long                                                                   clockTime;

    public final Map<Class< ? >, TimersOutputMarshaller>                           writersByClass;

    public final PrintStream                                                       out = System.out;

    private final ObjectMarshallingStrategyStore                                   objectMarshallingStrategyStore;
    private final Map<ObjectMarshallingStrategy, Integer>                           usedStrategies;
    private final Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> strategyContext;

    public final Map<LeftTuple, Integer>                                           terminalTupleMap;

    private final boolean                                                           marshalProcessInstances;
    private final boolean                                                           marshalWorkItems;
    private final Environment                                                       env;

    private Object                                                                 parameterObject;

    public ProtobufMarshallerWriteContext( OutputStream stream,
                                           InternalKnowledgeBase kBase,
                                           InternalWorkingMemory wm,
                                           Map<Integer, BaseNode> sinks,
                                           ObjectMarshallingStrategyStore resolverStrategyFactory,
                                           Environment env) throws IOException {
        this( stream,
              kBase,
              wm,
              sinks,
              resolverStrategyFactory,
              true,
              true,
              env );
    }

    public ProtobufMarshallerWriteContext( OutputStream stream,
                                           InternalKnowledgeBase kBase,
                                           InternalWorkingMemory wm,
                                           Map<Integer, BaseNode> sinks,
                                           ObjectMarshallingStrategyStore resolverStrategyFactory,
                                           boolean marshalProcessInstances,
                                           boolean marshalWorkItems,
                                           Environment env) throws IOException {
        super( stream );
        this.kBase = kBase;
        this.wm = wm;
        this.sinks = sinks;
        this.writersByClass = new HashMap<>();

        this.writersByClass.put( SlidingTimeWindow.BehaviorJobContext.class, new BehaviorJobContextTimerOutputMarshaller() );

        this.writersByClass.put( ExpireJobContext.class, new ExpireJobContextTimerOutputMarshaller() );
        
        this.writersByClass.put( TimerNodeJobContext.class, new TimerNodeTimerOutputMarshaller() );

        if ( resolverStrategyFactory == null ) {
            ObjectMarshallingStrategy[] strats = (ObjectMarshallingStrategy[]) env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
            if ( strats == null ) {
                strats = getMarshallingStrategy();
            }
            this.objectMarshallingStrategyStore = new ObjectMarshallingStrategyStoreImpl( strats );
        }
        else {
            this.objectMarshallingStrategyStore = resolverStrategyFactory;
        }
        this.usedStrategies = new HashMap<>();
        this.strategyContext = new HashMap<>();

        this.terminalTupleMap = new IdentityHashMap<>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
        this.env = env;

    }

    protected ObjectMarshallingStrategy[] getMarshallingStrategy() {
        return new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()};
    }

    @Override
    public Integer getStrategyIndex(ObjectMarshallingStrategy strategy) {
        Integer index = usedStrategies.get( strategy );
        if ( index == null ) {
            index = Integer.valueOf( usedStrategies.size() );
            usedStrategies.put( strategy, index );
            strategyContext.put( strategy, strategy.createContext() );
        }
        return index;
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    @Override
    public ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore() {
        return objectMarshallingStrategyStore;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameterObject( Object parameterObject ) {
        this.parameterObject = parameterObject;
    }

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return wm;
    }

    @Override
    public Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContext() {
        return strategyContext;
    }

    @Override
    public Map<ObjectMarshallingStrategy, Integer> getUsedStrategies() {
        return usedStrategies;
    }

    @Override
    public Map<Integer, BaseNode> getSinks() {
        return sinks;
    }

    @Override
    public boolean isMarshalProcessInstances() {
        return marshalProcessInstances;
    }

    @Override
    public boolean isMarshalWorkItems() {
        return marshalWorkItems;
    }

    @Override
    public Environment getEnvironment() {
        return env;
    }

    @Override
    public long getClockTime() {
        return clockTime;
    }

    @Override
    public void setClockTime( long clockTime ) {
        this.clockTime = clockTime;
    }

    @Override
    public Object getWriterForClass(Class<?> c) {
        return writersByClass.get( c );
    }

    @Override
    public void setWriterForClass(Class<?> c, Object writer) {
        writersByClass.put( c, (TimersOutputMarshaller ) writer );
    }
}
