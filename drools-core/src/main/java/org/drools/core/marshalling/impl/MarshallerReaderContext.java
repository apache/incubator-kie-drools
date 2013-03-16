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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.PBActivationsFilter;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPoint;
import org.drools.core.spi.PropagationContext;
import org.kie.marshalling.MarshallerFactory;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.marshalling.ObjectMarshallingStrategyStore;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KnowledgeRuntime;

public class MarshallerReaderContext extends ObjectInputStream {
    public final MarshallerReaderContext                                           stream;
    public final InternalRuleBase                                                  ruleBase;
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

    public final Map<String, EntryPoint>                                           entryPoints;

    public final Map<Integer, TimersInputMarshaller>                               readersByInt;

    public final Map<Long, PropagationContext>                                     propagationContexts;

    public final boolean                                                           marshalProcessInstances;
    public final boolean                                                           marshalWorkItems;
    public final Environment                                                       env;

    // this is a map to store node memory data indexed by node ID
    public final Map<Integer, Object>                                              nodeMemories;

    public Object                                                                  parameterObject;
    public ClassLoader                                                             classLoader;

    public MarshallerReaderContext(InputStream stream,
                                   InternalRuleBase ruleBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory,
                                   Map<Integer, TimersInputMarshaller> timerReaders,
                                   Environment env) throws IOException {
        this( stream,
              ruleBase,
              sinks,
              resolverStrategyFactory,
              timerReaders,
              true,
              true,
              env );
    }

    public MarshallerReaderContext(InputStream stream,
                                   InternalRuleBase ruleBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory,
                                   Map<Integer, TimersInputMarshaller> timerReaders,
                                   boolean marshalProcessInstances,
                                   boolean marshalWorkItems,
                                   Environment env) throws IOException {
        super( stream );
        this.stream = this;
        this.ruleBase = ruleBase;
        this.sinks = sinks;

        this.readersByInt = timerReaders;

        this.handles = new HashMap<Integer, InternalFactHandle>();
        this.rightTuples = new HashMap<RightTupleKey, RightTuple>();
        this.terminalTupleMap = new HashMap<Integer, LeftTuple>();
        this.filter = new PBActivationsFilter();
        this.entryPoints = new HashMap<String, EntryPoint>();
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

        this.parameterObject = null;
    }

    @Override
    protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException,
                                                             ClassNotFoundException {
        String name = desc.getName();
        try {
	    if(this.classLoader == null){
              if(this.ruleBase != null){
                  this.classLoader = this.ruleBase.getRootClassLoader();
              }
            }
            return Class.forName( name, false, this.classLoader );
        } catch ( ClassNotFoundException ex ) {
            return super.resolveClass( desc );
        }
    }
}
