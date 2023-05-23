package org.drools.core.util.index;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.constraint.BetaNodeFieldConstraint;

import static org.drools.core.util.index.IndexUtil.isBigDecimalEqualityConstraint;

public interface IndexFactory {

    static BetaMemory createBetaMemory(RuleBaseConfiguration config, short nodeType, BetaNodeFieldConstraint... constraints) {
        if (config.getCompositeKeyDepth() < 1) {
            return new BetaMemory( config.isSequential() ? null : new TupleList(),
                    new TupleList(),
                    createContext(constraints),
                    nodeType );
        }

        IndexSpec indexSpec = new IndexSpec(nodeType, constraints, config);
        return new BetaMemory( createLeftMemory(config, indexSpec),
                createRightMemory(config, indexSpec),
                createContext(constraints),
                nodeType );
    }

    private static TupleMemory createRightMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
        if ( !config.isIndexRightBetaMemory() || !indexSpec.getConstraintType().isIndexable() || indexSpec.getIndexes().length == 0 ) {
            return new TupleList();
        }

        if (indexSpec.getConstraintType() == IndexUtil.ConstraintType.EQUAL) {
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

        if (indexSpec.getConstraintType() == IndexUtil.ConstraintType.EQUAL) {
            return IndexMemory.createEqualityMemory(indexSpec, true);
        }

        if (indexSpec.getConstraintType().isComparison()) {
            return IndexMemory.createComparisonMemory(indexSpec, true);
        }

        return new TupleList();
    }

    private static ContextEntry[] createContext(BetaNodeFieldConstraint... constraints) {
        ContextEntry[] entries = new ContextEntry[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            entries[i] = constraints[i].createContextEntry();
        }
        return entries;
    }
}
