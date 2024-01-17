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
package org.drools.core.util.index;

import org.drools.base.rule.ContextEntry;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.TupleMemory;
import org.drools.base.rule.constraint.BetaConstraint;

public interface IndexFactory {

    static BetaMemory createBetaMemory(RuleBaseConfiguration config, int nodeType, BetaConstraint... constraints) {
        if (config.getCompositeKeyDepth() < 1) {
            return new BetaMemory(config.isSequential() ? null : new TupleList(),
                                  new TupleList(),
                                  createContext(constraints),
                                  nodeType );
        }

        IndexSpec indexSpec = new IndexSpec(nodeType, constraints, config);
        return new BetaMemory(createLeftMemory(config, indexSpec),
                              createRightMemory(config, indexSpec),
                              createContext(constraints),
                              nodeType );
    }

    private static TupleMemory createRightMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
        if ( !config.isIndexRightBetaMemory() || !indexSpec.getConstraintType().isIndexable() || indexSpec.getIndexes().length == 0 ) {
            return new TupleList();
        }

        if (indexSpec.getConstraintType() == ConstraintTypeOperator.EQUAL) {
            return IndexMemory.createEqualityMemory(indexSpec, false);
        }

        if (indexSpec.getConstraintType().isComparison()) {
            return IndexMemory.createComparisonMemory(indexSpec, false);
        }

        return new TupleList();
    }

    private static TupleMemory createLeftMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
        if (config.isSequential()) {
            return null;
        }
        if ( !config.isIndexLeftBetaMemory() || !indexSpec.getConstraintType().isIndexable() || indexSpec.getIndexes().length == 0 ) {
            return new TupleList();
        }

        if (indexSpec.getConstraintType() == ConstraintTypeOperator.EQUAL) {
            return IndexMemory.createEqualityMemory(indexSpec, true);
        }

        if (indexSpec.getConstraintType().isComparison()) {
            return IndexMemory.createComparisonMemory(indexSpec, true);
        }

        return new TupleList();
    }

    private static Object createContext(BetaConstraint... constraints) {
        if (constraints.length == 1) {
            // no array needed
            return constraints[0].createContext();
        }

        Object[] entries = new ContextEntry[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            entries[i] = constraints[i].createContext();
        }
        return entries;
    }
}
