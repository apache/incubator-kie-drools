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

import java.io.ObjectOutput;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalRuleBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;

public interface MarshallerWriteContext extends ObjectOutput {

    InternalRuleBase getKnowledgeBase();

    ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore();

    Object getParameterObject();
    void setParameterObject( Object parameterObject );

    InternalWorkingMemory getWorkingMemory();

    Map<ObjectMarshallingStrategy, ObjectMarshallingStrategy.Context> getStrategyContext();

    Map<ObjectMarshallingStrategy, Integer> getUsedStrategies();

    Map<Integer, BaseNode> getSinks();

    long getClockTime();
    void setClockTime( long clockTime );

    boolean isMarshalProcessInstances();

    boolean isMarshalWorkItems();

    Environment getEnvironment();

    Integer getStrategyIndex( ObjectMarshallingStrategy strategy);

    Object getWriterForClass(Class<?> c);
    void setWriterForClass(Class<?> c, Object writer);
}
