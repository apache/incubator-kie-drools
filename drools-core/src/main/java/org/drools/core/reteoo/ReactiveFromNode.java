/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.From;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
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

    public ReactiveFromMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        BetaMemory beta = new BetaMemory( new TupleList(),
                                          null,
                                          this.betaConstraints.createContext(),
                                          NodeTypeEnums.FromNode );
        return new ReactiveFromMemory( beta,
                                       this.dataProvider );
    }

    public short getType() {
        return NodeTypeEnums.ReactiveFromNode;
    } 

    public static class ReactiveFromMemory extends FromNode.FromMemory {

        private static final long serialVersionUID = 510l;

        private final TupleSets<LeftTuple> stagedLeftTuples;

        public ReactiveFromMemory(BetaMemory betaMemory,
                                  DataProvider dataProvider) {
            super(betaMemory, dataProvider);
            stagedLeftTuples = new TupleSetsImpl<LeftTuple>();
        }

        public short getNodeType() {
            return NodeTypeEnums.ReactiveFromNode;
        }

        public TupleSets<LeftTuple> getStagedLeftTuples() {
            return stagedLeftTuples;
        }
    }

    @Override
    public String toString() {
        return "[ReactiveFromNode(" + id + ") :: " + dataProvider + "]";
    }
}
