/*
 * Copyright 2010 JBoss Inc
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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Scheduler.ActivationTimerJobContext;
import org.drools.core.common.Scheduler.ActivationTimerOutputMarshaller;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeJobContext;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeTimerOutputMarshaller;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContextTimerOutputMarshaller;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.SlidingTimeWindow.BehaviorJobContextTimerOutputMarshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.marshalling.MarshallerFactory;

public class MarshallerWriteContext extends ObjectOutputStream {
    public final MarshallerWriteContext                                            stream;
    public final InternalKnowledgeBase                                             kBase;
    public final InternalWorkingMemory                                             wm;
    public final Map<Integer, BaseNode>                                            sinks;

    public long                                                                    clockTime;

    public final Map<Class< ? >, TimersOutputMarshaller>                           writersByClass;

    public final PrintStream                                                       out = System.out;

    public final ObjectMarshallingStrategyStore                                    objectMarshallingStrategyStore;
    public final Map<ObjectMarshallingStrategy, Integer>                           usedStrategies;
    public final Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> strategyContext;

    public final Map<LeftTuple, Integer>                                           terminalTupleMap;

    public final boolean                                                           marshalProcessInstances;
    public final boolean                                                           marshalWorkItems;
    public final Environment                                                       env;

    public Object                                                                  parameterObject;

    public MarshallerWriteContext(OutputStream stream,
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

    public MarshallerWriteContext(OutputStream stream,
                                  InternalKnowledgeBase kBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  ObjectMarshallingStrategyStore resolverStrategyFactory,
                                  boolean marshalProcessInstances,
                                  boolean marshalWorkItems,
                                  Environment env) throws IOException {
        super( stream );
        this.stream = this;
        this.kBase = kBase;
        this.wm = wm;
        this.sinks = sinks;
        this.writersByClass = new HashMap<Class< ? >, TimersOutputMarshaller>();

        this.writersByClass.put( SlidingTimeWindow.BehaviorJobContext.class, new BehaviorJobContextTimerOutputMarshaller() );

        this.writersByClass.put( ActivationTimerJobContext.class, new ActivationTimerOutputMarshaller() );

        this.writersByClass.put( ExpireJobContext.class, new ExpireJobContextTimerOutputMarshaller() );
        
        this.writersByClass.put( TimerNodeJobContext.class, new TimerNodeTimerOutputMarshaller() );

        if ( resolverStrategyFactory == null ) {
            ObjectMarshallingStrategy[] strats = (ObjectMarshallingStrategy[]) env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
            if ( strats == null ) {
                strats = new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()};
            }
            this.objectMarshallingStrategyStore = new ObjectMarshallingStrategyStoreImpl( strats );
        }
        else {
            this.objectMarshallingStrategyStore = resolverStrategyFactory;
        }
        this.usedStrategies = new HashMap<ObjectMarshallingStrategy, Integer>();
        this.strategyContext = new HashMap<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context>();

        this.terminalTupleMap = new IdentityHashMap<LeftTuple, Integer>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
        this.env = env;

    }

    public Integer getStrategyIndex(ObjectMarshallingStrategy strategy) {
        Integer index = usedStrategies.get( strategy );
        if ( index == null ) {
            index = Integer.valueOf( usedStrategies.size() );
            usedStrategies.put( strategy, index );
            strategyContext.put( strategy, strategy.createContext() );
        }
        return index;
    }

}
