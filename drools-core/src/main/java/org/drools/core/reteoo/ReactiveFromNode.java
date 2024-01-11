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

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.From;
import org.drools.base.rule.accessor.DataProvider;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.index.TupleList;

public class ReactiveFromNode extends FromNode<ReactiveFromNode.ReactiveFromMemory> {
    public ReactiveFromNode() { }

    public ReactiveFromNode(final int id,
                            final DataProvider dataProvider,
                            final LeftTupleSource tupleSource,
                            final AlphaNodeFieldConstraint[] constraints,
                            final BetaConstraints binder,
                            final boolean tupleMemoryEnabled,
                            final BuildContext context,
                            final From from) {
        super(id, dataProvider, tupleSource, constraints, binder, tupleMemoryEnabled, context, from);
    }

    public ReactiveFromMemory createMemory(final RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        BetaMemoryImpl beta = new BetaMemoryImpl(new TupleList(),
                                                 null,
                                                 this.betaConstraints.createContext(),
                                                 NodeTypeEnums.FromNode );
        return new ReactiveFromMemory( beta,
                                       this.dataProvider );
    }

    public int getType() {
        return NodeTypeEnums.ReactiveFromNode;
    } 

    public static class ReactiveFromMemory extends FromNode.FromMemory {

        private static final long serialVersionUID = 510l;

        private final TupleSets stagedLeftTuples;

        public ReactiveFromMemory(BetaMemoryImpl betaMemory,
                                  DataProvider dataProvider) {
            super(betaMemory, dataProvider);
            stagedLeftTuples = new TupleSetsImpl();
        }

        public int getNodeType() {
            return NodeTypeEnums.ReactiveFromNode;
        }

        public TupleSets getStagedLeftTuples() {
            return stagedLeftTuples;
        }
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new ReactiveFromNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final TupleImpl leftTuple,
                                     final Sink sink) {
        return new ReactiveFromNodeLeftTuple(factHandle, leftTuple, sink );
    }

    public LeftTuple createLeftTuple(TupleImpl leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled ) {
        throw new UnsupportedOperationException();
    }

    public LeftTuple createLeftTuple(TupleImpl leftTuple,
                                     TupleImpl rightTuple,
                                     Sink sink) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LeftTuple createLeftTuple(TupleImpl leftTuple,
                                     TupleImpl rightTuple,
                                     TupleImpl currentLeftChild,
                                     TupleImpl currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new ReactiveFromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    @Override
    public String toString() {
        return "[ReactiveFromNode(" + id + ") :: " + dataProvider + "]";
    }

    @Override
    public LeftTuple createPeer(TupleImpl original) {
        ReactiveFromNodeLeftTuple peer = new ReactiveFromNodeLeftTuple();
        peer.initPeer((LeftTuple) original, this);
        original.setPeer( peer );
        return peer;
    }
}
