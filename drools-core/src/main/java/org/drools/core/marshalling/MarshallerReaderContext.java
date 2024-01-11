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
package org.drools.core.marshalling;

import java.io.ObjectInput;
import java.util.Map;

import org.drools.base.rule.EntryPointId;
import org.drools.core.common.ActivationsFilter;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.phreak.PhreakTimerNode.Scheduler;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.TupleImpl ;
import org.drools.core.reteoo.TupleImpl;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

public interface MarshallerReaderContext extends ObjectInput {

    void addTimerNodeScheduler( int nodeId, TupleKey key, Scheduler scheduler );
    Scheduler removeTimerNodeScheduler( int nodeId, TupleKey key );

    InternalWorkingMemory getWorkingMemory();
    InternalRuleBase getKnowledgeBase();
    Map<Long, InternalFactHandle> getHandles();
    Map<Integer, LeftTuple> getTerminalTupleMap();
    ActivationsFilter getFilter();
    Map<Integer, BaseNode> getSinks();
    Map<Long, PropagationContext> getPropagationContexts();
    Map<Integer, Object> getNodeMemories();
    ObjectMarshallingStrategyStore getResolverStrategyFactory();
    ClassLoader getClassLoader();
    Map<Integer, ObjectMarshallingStrategy> getUsedStrategies();
    Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContexts();

    Object getParameterObject();
    void setParameterObject( Object parameterObject );

    Object getReaderForInt(int i);
    void setReaderForInt(int i, Object reader);

    InternalFactHandle createAccumulateHandle(EntryPointId entryPointId, ReteEvaluator reteEvaluator, TupleImpl leftTuple, Object result, int nodeId);
    InternalFactHandle createAsyncNodeHandle( TupleImpl  leftTuple, ReteEvaluator reteEvaluator, Object object, int nodeId, ObjectTypeConf objectTypeConf );
    QueryElementFactHandle createQueryResultHandle( TupleImpl  leftTuple, Object[] objects, int nodeId );
    InternalFactHandle createQueryHandle(TupleImpl  leftTuple, ReteEvaluator reteEvaluator, int nodeId );
}
