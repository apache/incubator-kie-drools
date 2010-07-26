/**
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

/**
 * 
 */
package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;

public class MarshallerWriteContext extends ObjectOutputStream {
    public final MarshallerWriteContext         stream;
    public final InternalRuleBase               ruleBase;
    public final InternalWorkingMemory          wm;
    public final Map<Integer, BaseNode>         sinks;

    public final PrintStream                    out = System.out;

    public final ObjectMarshallingStrategyStore objectMarshallingStrategyStore;

    public final Map<LeftTuple, Integer>        terminalTupleMap;

    public final boolean                        marshalProcessInstances;
    public final boolean                        marshalWorkItems;

    public MarshallerWriteContext(OutputStream stream,
                                  InternalRuleBase ruleBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  ObjectMarshallingStrategyStore resolverStrategyFactory) throws IOException {
        this( stream,
              ruleBase,
              wm,
              sinks,
              resolverStrategyFactory,
              true,
              true );
    }

    public MarshallerWriteContext(OutputStream stream,
                                  InternalRuleBase ruleBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  ObjectMarshallingStrategyStore resolverStrategyFactory,
                                  boolean marshalProcessInstances,
                                  boolean marshalWorkItems) throws IOException {
        super( stream );
        this.stream = this;
        this.ruleBase = ruleBase;
        this.wm = wm;
        this.sinks = sinks;

        this.objectMarshallingStrategyStore = resolverStrategyFactory;

        this.terminalTupleMap = new IdentityHashMap<LeftTuple, Integer>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
    }
}