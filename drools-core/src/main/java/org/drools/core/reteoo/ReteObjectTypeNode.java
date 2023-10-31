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
package org.drools.core.reteoo;

import org.drools.base.base.ObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.builder.BuildContext;

public class ReteObjectTypeNode extends ObjectTypeNode {

    public ReteObjectTypeNode() {
    }

    public ReteObjectTypeNode(int id, EntryPointNode source, ObjectType objectType, BuildContext context) {
        super(id, source, objectType, context);
    }

    public void doAttach( BuildContext context ) {
        super.doAttach( context );
        if (context == null ) {
            return;
        }

        // we need to call updateSink on Rete, because someone
        // might have already added facts matching this ObjectTypeNode
        // to working memories
        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.Type.RULE_ADDITION,
                                                                                               null, null, null);
            propagationContext.setEntryPoint( ((EntryPointNode) this.source).getEntryPoint() );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }
}
