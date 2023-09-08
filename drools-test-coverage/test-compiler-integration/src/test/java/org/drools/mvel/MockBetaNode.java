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
package org.drools.mvel;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Memory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.JoinNodeLeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;

public class MockBetaNode extends BetaNode {
    
    public MockBetaNode() {
        
    }

    @Override
    protected boolean doRemove( RuleRemovalContext context, ReteooBuilder builder) {
        return true;
    }

    MockBetaNode(final int id,
                 final LeftTupleSource leftInput,
                 final ObjectSource rightInput,
                 BuildContext buildContext) {
        super( id,
               leftInput,
               rightInput,
               EmptyBetaConstraints.getInstance(),
               buildContext );
    }        

    MockBetaNode(final int id,
                 final LeftTupleSource leftInput,
                 final ObjectSource rightInput) {
        super( id,
               leftInput,
               rightInput,
               EmptyBetaConstraints.getInstance(),
               null );
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext pctx,
                             final ReteEvaluator reteEvaluator) {
    }

    @Override
    public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final ReteEvaluator reteEvaluator) {
    }

    public short getType() {
        return 0;
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new JoinNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
    public Memory createMemory(RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        return super.createMemory( config, reteEvaluator);
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }                
}