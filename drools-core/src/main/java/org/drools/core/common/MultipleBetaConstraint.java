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
package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.IndexableConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.util.index.IndexFactory;
import org.kie.internal.conf.IndexPrecedenceOption;

import static org.drools.base.util.index.IndexUtil.compositeAllowed;
import static org.drools.base.util.index.IndexUtil.isIndexableForNode;

public abstract class MultipleBetaConstraint implements BetaConstraints<ContextEntry[]> {
    protected BetaConstraint<ContextEntry>[] constraints;
    protected boolean[]                      indexed;
    protected IndexPrecedenceOption     indexPrecedenceOption;
    protected transient boolean         disableIndexing;

    private transient Boolean           leftUpdateOptimizationAllowed;

    public MultipleBetaConstraint() { }

    public MultipleBetaConstraint( BetaConstraint[] constraints,
                                   RuleBaseConfiguration conf,
                                   boolean disableIndexing) {
        this(constraints, conf.getIndexPrecedenceOption(), disableIndexing);
    }

    protected MultipleBetaConstraint( BetaConstraint[] constraints,
                                      IndexPrecedenceOption indexPrecedenceOption,
                                      boolean disableIndexing) {
        this.constraints = constraints;
        this.indexPrecedenceOption = indexPrecedenceOption;
        this.disableIndexing = disableIndexing;
    }

    public final void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraints = (BetaConstraint[])in.readObject();
        indexed = (boolean[]) in.readObject();
        indexPrecedenceOption = (IndexPrecedenceOption) in.readObject();
    }

    public final void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraints);
        out.writeObject(indexed);
        out.writeObject(indexPrecedenceOption);
    }

    public final void init(BuildContext context, int betaNodeType) {
        RuleBaseConfiguration config = context.getRuleBase().getRuleBaseConfiguration();

        if ( disableIndexing || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory()) ) {
            indexed = new boolean[constraints.length];
        } else {
            int depth = config.getCompositeKeyDepth();
            if ( !compositeAllowed( constraints, betaNodeType, config ) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }
            initIndexes( depth, betaNodeType, config );
        }
    }

    public final void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config) {
        indexed = isIndexableForNode(indexPrecedenceOption, betaNodeType, depth, constraints, config);
    }

    public final boolean isIndexed() {
        return indexed[0];
    }

    public final int getIndexCount() {
        int count = 0;
        for (boolean i : indexed) {
            if ( i ) {
                count++;
            }
        }
        return count;
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                       final int nodeType) {
        return IndexFactory.createBetaMemory(config, nodeType, constraints);
    }

    public final BetaConstraint[] getConstraints() {
        return constraints;
    }

    public final ContextEntry[] createContext() {
        ContextEntry[] entries = new ContextEntry[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            entries[i] = constraints[i].createContext();
        }
        return entries;
    }

    public final boolean isEmpty() {
        return false;
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        if (leftUpdateOptimizationAllowed == null) {
            leftUpdateOptimizationAllowed = calcLeftUpdateOptimizationAllowed();
        }
        return leftUpdateOptimizationAllowed;
    }

    private boolean calcLeftUpdateOptimizationAllowed() {
        for (BetaConstraint constraint : constraints) {
            if ( !(constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).getConstraintType().isEquality()) ) {
                return false;
            }
        }
        return true;
    }

    public void registerEvaluationContext(BuildContext buildContext) {
        for (int i = 0; i < constraints.length; i++) {
            constraints[i].registerEvaluationContext(buildContext);
        }
    }
}
