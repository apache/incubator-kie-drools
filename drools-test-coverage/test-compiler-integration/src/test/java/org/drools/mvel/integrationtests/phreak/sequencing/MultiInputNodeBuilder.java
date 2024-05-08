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
package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectRouter;
import org.drools.core.reteoo.SequenceNode;
import org.drools.core.reteoo.SequenceNode.AlphaAdapter;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.NodeFactory;
import org.kie.api.runtime.rule.FactHandle;

public class MultiInputNodeBuilder {
    BuildContext buildContext;

    public MultiInputNodeBuilder(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public static MultiInputNodeBuilder create(BuildContext buildContext) {
        return new MultiInputNodeBuilder(buildContext);
    }

    public SequenceNode buildNode(Class leftType, Class[] rightTypes) {
        NodeFactory nFactory = CoreComponentFactory.get().getNodeFactoryService();

        EntryPointNode epn = buildContext.getRuleBase().getRete().getEntryPointNodes().get(EntryPointId.DEFAULT);

        buildContext.setCurrentEntryPoint(epn.getEntryPoint());
        buildContext.setTupleMemoryEnabled(true);
        buildContext.setPartitionId(RuleBasePartitionId.MAIN_PARTITION);

        ObjectTypeNode otn = nFactory.buildObjectTypeNode(buildContext.getNextNodeId(),
                                                          epn,
                                                          new ClassObjectType(leftType),
                                                          buildContext);
        LeftTupleSource leftTs = nFactory.buildLeftInputAdapterNode(buildContext.getNextNodeId(), otn,
                                                                 buildContext, false);

        ObjectSource[] otns = new ObjectSource[rightTypes.length];
        for ( int i = 0; i < rightTypes.length; i++ ) {
            otns[i] = nFactory.buildObjectTypeNode(buildContext.getNextNodeId(),  epn,
                                                   new ClassObjectType(rightTypes[i]),
                                                   buildContext);

            otns[i].attach(buildContext);
        }


        SequenceNode mn = new SequenceNode(buildContext.getNextNodeId(),
                                           leftTs, buildContext);

        AlphaAdapter[] adapters = new AlphaAdapter[rightTypes.length];
        for ( int i = 0; i < rightTypes.length; i++ ) {
            adapters[i] = new AlphaAdapter(buildContext.getNextNodeId(), otns[i],
                                           buildContext.getPartitionId(), mn, i);

            adapters[i].attach(buildContext);
        }

        mn.setAlphaAdapters(adapters);

        mn.attach(buildContext);

        return mn;
    }

    public AlphaNodeFieldConstraint buildConstraint(Predicate1 predicate1) {
        return new AlphaConstraint(predicate1);
    }

    public static class AlphaConstraint extends MutableTypeConstraint {
        private Predicate1 predicate1;

        public AlphaConstraint(Predicate1 predicate1) {
            this.predicate1 = predicate1;
        }

        @Override
        public Declaration[] getRequiredDeclarations() {
            return new Declaration[0];
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

        }

        @Override
        public MutableTypeConstraint clone() {
            return null;
        }

        @Override
        public boolean isTemporal() {
            return false;
        }

        @Override
        public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
            return predicate1.test(handle.getObject());
        }

        @Override
        public boolean isAllowedCachedLeft(Object context, FactHandle handle) {
            return false;
        }

        @Override
        public boolean isAllowedCachedRight(BaseTuple tuple, Object context) {
            return false;
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    interface Predicate1<A>  {
        boolean test(A a);
    }

}
