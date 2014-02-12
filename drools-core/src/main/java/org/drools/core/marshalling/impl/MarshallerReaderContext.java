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

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.PBActivationsFilter;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.core.phreak.PhreakTimerNode.Scheduler;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.KnowledgeRuntime;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

public class MarshallerReaderContext extends ObjectInputStream {
    public final MarshallerReaderContext                                           stream;
    public final InternalKnowledgeBase                                             kBase;
    public InternalWorkingMemory                                                   wm;
    public KnowledgeRuntime                                                        kruntime;
    public final Map<Integer, BaseNode>                                            sinks;

    public Map<Integer, InternalFactHandle>                                        handles;

    public final Map<RightTupleKey, RightTuple>                                    rightTuples;
    public final Map<Integer, LeftTuple>                                           terminalTupleMap;
    public final PBActivationsFilter                                               filter;

    public final ObjectMarshallingStrategyStore                                    resolverStrategyFactory;
    public final Map<Integer, ObjectMarshallingStrategy>                           usedStrategies;
    public final Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> strategyContexts;

    public final Map<String, EntryPointId>                                           entryPoints;

    public final Map<Integer, TimersInputMarshaller>                               readersByInt;

    public final Map<Long, PropagationContext>                                     propagationContexts;

    public final boolean                                                           marshalProcessInstances;
    public final boolean                                                           marshalWorkItems;
    public final Environment                                                       env;

    // this is a map to store node memory data indexed by node ID
    public final Map<Integer, Object>                                              nodeMemories;

    public Object                                                                  parameterObject;
    public ClassLoader                                                             classLoader;
    public Map<Integer, Map<TupleKey, Scheduler>>                                  timerNodeSchedulers;

    public MarshallerReaderContext(InputStream stream,
                                   InternalKnowledgeBase kBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory,
                                   Map<Integer, TimersInputMarshaller> timerReaders,
                                   Environment env) throws IOException {
        this( stream,
              kBase,
              sinks,
              resolverStrategyFactory,
              timerReaders,
              true,
              true,
              env );
    }

    public MarshallerReaderContext(InputStream stream,
                                   InternalKnowledgeBase kBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory,
                                   Map<Integer, TimersInputMarshaller> timerReaders,
                                   boolean marshalProcessInstances,
                                   boolean marshalWorkItems,
                                   Environment env) throws IOException {
        super( stream );
        this.stream = this;
        this.kBase = kBase;
        this.sinks = sinks;

        this.readersByInt = timerReaders;

        this.handles = new HashMap<Integer, InternalFactHandle>();
        this.rightTuples = new HashMap<RightTupleKey, RightTuple>();
        this.terminalTupleMap = new HashMap<Integer, LeftTuple>();
        this.filter = new PBActivationsFilter();
        this.entryPoints = new HashMap<String, EntryPointId>();
        this.propagationContexts = new HashMap<Long, PropagationContext>();
        if ( resolverStrategyFactory == null ) {
            ObjectMarshallingStrategy[] strats = (ObjectMarshallingStrategy[]) env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
            if ( strats == null ) {
                strats = new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()};
            }
            this.resolverStrategyFactory = new ObjectMarshallingStrategyStoreImpl( strats );
        }
        else {
            this.resolverStrategyFactory = resolverStrategyFactory;
        }
        this.usedStrategies = new HashMap<Integer, ObjectMarshallingStrategy>();
        this.strategyContexts = new HashMap<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
        this.env = env;

        this.nodeMemories = new HashMap<Integer, Object>();
        this.timerNodeSchedulers = new HashMap<Integer, Map<ProtobufInputMarshaller.TupleKey, PhreakTimerNode.Scheduler>>();

        this.parameterObject = null;
    }

    @Override
    protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException,
                                                             ClassNotFoundException {
        String name = desc.getName();
        try {
            if ( this.classLoader == null ) {
                if ( this.kBase != null ) {
                    this.classLoader = this.kBase.getRootClassLoader();
                }
            }
            return Class.forName( name, false, this.classLoader );
        } catch ( ClassNotFoundException ex ) {
            return super.resolveClass( desc );
        }
    }
    
    public void addTimerNodeScheduler( int nodeId, TupleKey key, Scheduler scheduler ) {
        Map<TupleKey, Scheduler> timers = timerNodeSchedulers.get( nodeId );
        if( timers == null ) {
            timers = new HashMap<ProtobufInputMarshaller.TupleKey, PhreakTimerNode.Scheduler>();
            timerNodeSchedulers.put( nodeId, timers );
        }
        timers.put( key, scheduler );
    }
    
    public Scheduler removeTimerNodeScheduler( int nodeId, TupleKey key ) {
        Map<TupleKey, Scheduler> timers = timerNodeSchedulers.get( nodeId );
        if( timers != null ) {
            Scheduler scheduler = timers.remove( key );
            if( timers.isEmpty() ) {
                timerNodeSchedulers.remove( nodeId );
            }
            return scheduler;
        } 
        return null;
    }
}
