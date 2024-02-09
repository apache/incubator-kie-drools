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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.TupleKey;
import org.drools.core.phreak.PhreakTimerNode.Scheduler;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.base.rule.EntryPointId;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.TupleImpl ;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.serialization.protobuf.ProtobufInputMarshaller.PBActivationsFilter;
import org.drools.serialization.protobuf.marshalling.ObjectMarshallingStrategyStoreImpl;
import org.drools.serialization.protobuf.marshalling.RightTupleKey;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.marshalling.MarshallerFactory;

public class ProtobufMarshallerReaderContext extends ObjectInputStream implements MarshallerReaderContext {

    private final InternalKnowledgeBase kBase;
    private InternalWorkingMemory                                                  wm;
    public KieRuntime                                                              kruntime;
    private final Map<Integer, BaseNode>                                           sinks;

    private Map<Long, InternalFactHandle>                                          handles;

    public final Map<RightTupleKey, RightTuple> rightTuples;
    private final Map<Integer, LeftTuple>       terminalTupleMap;
    private final PBActivationsFilter                                              filter;

    private final ObjectMarshallingStrategyStore                                   resolverStrategyFactory;
    private final Map<Integer, ObjectMarshallingStrategy>                           usedStrategies;
    private final Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> strategyContexts;

    public final Map<String, EntryPointId>                                         entryPoints;

    private final Map<Integer, TimersInputMarshaller>                              readersByInt;

    private final Map<Long, PropagationContext>                                    propagationContexts;

    public final boolean                                                           marshalProcessInstances;
    public final boolean                                                           marshalWorkItems;
    public final Environment                                                       env;

    // this is a map to store node memory data indexed by node ID
    private final Map<Integer, Object>                                              nodeMemories;

    @Override
    public Map<Integer, Object> getNodeMemories() {
        return nodeMemories;
    }

    private Object                                                                 parameterObject;
    private ClassLoader                                                            classLoader;
    public Map<Integer, Map<TupleKey, Scheduler>>                                  timerNodeSchedulers;

    public ProtobufMarshallerReaderContext( InputStream stream,
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

    public ProtobufMarshallerReaderContext( InputStream stream,
                                            InternalKnowledgeBase kBase,
                                            Map<Integer, BaseNode> sinks,
                                            ObjectMarshallingStrategyStore resolverStrategyFactory,
                                            Map<Integer, TimersInputMarshaller> timerReaders,
                                            boolean marshalProcessInstances,
                                            boolean marshalWorkItems,
                                            Environment env) throws IOException {
        super( stream );
        this.kBase = kBase;
        this.sinks = sinks;

        this.readersByInt = timerReaders;

        this.handles = new HashMap<>();
        this.rightTuples = new HashMap<>();
        this.terminalTupleMap = new HashMap<>();
        this.filter = new PBActivationsFilter();
        this.entryPoints = new HashMap<>();
        this.propagationContexts = new HashMap<>();
        if ( resolverStrategyFactory == null ) {
            ObjectMarshallingStrategy[] strats = (ObjectMarshallingStrategy[]) env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
            if ( strats == null ) {
                strats = getMarshallingStrategy();
            }
            this.resolverStrategyFactory = new ObjectMarshallingStrategyStoreImpl( strats );
        }
        else {
            this.resolverStrategyFactory = resolverStrategyFactory;
        }
        this.usedStrategies = new HashMap<>();
        this.strategyContexts = new HashMap<>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
        this.env = env;

        this.nodeMemories = new HashMap<>();
        this.timerNodeSchedulers = new HashMap<>();

        this.parameterObject = null;
    }

    protected ObjectMarshallingStrategy[] getMarshallingStrategy() {
        return new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()};
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

    @Override
    public void addTimerNodeScheduler( int nodeId, TupleKey key, Scheduler scheduler ) {
        Map<TupleKey, Scheduler> timers = timerNodeSchedulers.get( nodeId );
        if( timers == null ) {
            timers = new HashMap<>();
            timerNodeSchedulers.put( nodeId, timers );
        }
        timers.put( key, scheduler );
    }

    @Override
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

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return wm;
    }

    public void setWorkingMemory( InternalWorkingMemory wm ) {
        this.wm = wm;
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    @Override
    public Map<Long, InternalFactHandle> getHandles() {
        return handles;
    }

    @Override
    public Map<Integer, LeftTuple> getTerminalTupleMap() {
        return terminalTupleMap;
    }

    @Override
    public PBActivationsFilter getFilter() {
        return filter;
    }

    @Override
    public Map<Integer, BaseNode> getSinks() {
        return sinks;
    }

    @Override
    public Map<Long, PropagationContext> getPropagationContexts() {
        return propagationContexts;
    }

    @Override
    public ObjectMarshallingStrategyStore getResolverStrategyFactory() {
        return resolverStrategyFactory;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader( ClassLoader classLoader ) {
        this.classLoader = classLoader;
    }

    @Override
    public Map<Integer, ObjectMarshallingStrategy> getUsedStrategies() {
        return usedStrategies;
    }

    @Override
    public Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContexts() {
        return strategyContexts;
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
    public Object getReaderForInt(int i) {
        return readersByInt.get( i );
    }

    @Override
    public void setReaderForInt(int i, Object reader) {
        readersByInt.put( i, (TimersInputMarshaller ) reader );
    }

    @Override
    public InternalFactHandle createAccumulateHandle(EntryPointId entryPointId, ReteEvaluator reteEvaluator,
                                                     TupleImpl leftTuple, Object result, int nodeId) {
        InternalFactHandle handle = null;
        ProtobufMessages.FactHandle _handle = null;
        Map<TupleKey, ProtobufMessages.FactHandle> map = (Map<TupleKey, ProtobufMessages.FactHandle>) getNodeMemories().get( nodeId );
        if( map != null ) {
            _handle = map.get( TupleKey.createTupleKey(leftTuple) );
        }

        if( _handle != null ) {
            // create a handle with the given id
            handle = reteEvaluator.getFactHandleFactory().newFactHandle( _handle.getId(),
                    result,
                    _handle.getRecency(),
                    reteEvaluator.getDefaultEntryPoint().getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf( entryPointId,
                            result ),
                    reteEvaluator,
                    null ); // so far, result is not an event
        }
        return handle;
    }

    @Override
    public InternalFactHandle createAsyncNodeHandle( TupleImpl  leftTuple, ReteEvaluator reteEvaluator,
                                                     Object object, int nodeId, ObjectTypeConf objectTypeConf ) {
        ProtobufMessages.FactHandle _handle = null;
        Map<TupleKey, List<ProtobufMessages.FactHandle>> map = (Map<TupleKey, List<ProtobufMessages.FactHandle>>) getNodeMemories().get( nodeId );
        if( map != null ) {
            TupleKey key = TupleKey.createTupleKey( leftTuple );
            List<ProtobufMessages.FactHandle> list = map.get( key );
            if( list != null && ! list.isEmpty() ) {
                // it is a linked list, so the operation is fairly efficient
                _handle = ((java.util.LinkedList<ProtobufMessages.FactHandle>)list).removeFirst();
                if( list.isEmpty() ) {
                    map.remove(key);
                }
            }
        }

        InternalFactHandle handle = null;
        if( _handle != null ) {
            // create a handle with the given id
            handle = reteEvaluator.getFactHandleFactory().newFactHandle( _handle.getId(),
                    object,
                    _handle.getRecency(),
                    objectTypeConf,
                    reteEvaluator,
                    null );
        }
        return handle;
    }

    @Override
    public QueryElementFactHandle createQueryResultHandle(TupleImpl  leftTuple, Object[] objects, int nodeId ) {
        ProtobufMessages.FactHandle handle = null;
        Map<TupleKey, ProtobufInputMarshaller.QueryElementContext> map = (Map<TupleKey, ProtobufInputMarshaller.QueryElementContext>) getNodeMemories().get( nodeId );
        if( map != null ) {
            ProtobufInputMarshaller.QueryElementContext queryElementContext = map.get( TupleKey.createTupleKey( leftTuple ) );
            if( queryElementContext != null ) {
                handle = queryElementContext.results.removeFirst();
            }
        }

        return handle != null ?
                new QueryElementFactHandle( objects,
                        handle.getId(),
                        handle.getRecency() ) :
                null;
    }

    @Override
    public InternalFactHandle createQueryHandle(TupleImpl  leftTuple, ReteEvaluator reteEvaluator, int nodeId ) {
        ProtobufMessages.FactHandle handle = null;
        Map<TupleKey, ProtobufInputMarshaller.QueryElementContext> map = (Map<TupleKey, ProtobufInputMarshaller.QueryElementContext>) getNodeMemories().get( nodeId );
        if( map != null ) {
            handle = map.get( TupleKey.createTupleKey( leftTuple ) ).handle;
        }

        return handle != null ?
                reteEvaluator.getFactHandleFactory().newFactHandle( handle.getId(),
                        null,
                        handle.getRecency(),
                        null,
                        reteEvaluator,
                        null ) :
                null;
    }

    public void withSerializedNodeMemories() {
        filter.withSerializedNodeMemories();
    }
}
