package org.drools.core.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.kie.internal.conf.IndexPrecedenceOption;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.drools.core.util.index.IndexUtil.compositeAllowed;
import static org.drools.core.util.index.IndexUtil.isIndexableForNode;

public abstract class MultipleBetaConstraint implements BetaConstraints {
    protected BetaNodeFieldConstraint[] constraints;
    protected boolean[]                 indexed;
    protected IndexPrecedenceOption     indexPrecedenceOption;
    protected transient boolean         disableIndexing;

    private transient Boolean           leftUpdateOptimizationAllowed;

    public MultipleBetaConstraint() { }

    public MultipleBetaConstraint( BetaNodeFieldConstraint[] constraints,
                                   RuleBaseConfiguration conf,
                                   boolean disableIndexing) {
        this(constraints, conf.getIndexPrecedenceOption(), disableIndexing);
    }

    protected MultipleBetaConstraint( BetaNodeFieldConstraint[] constraints,
                                      IndexPrecedenceOption indexPrecedenceOption,
                                      boolean disableIndexing) {
        this.constraints = constraints;
        this.indexPrecedenceOption = indexPrecedenceOption;
        this.disableIndexing = disableIndexing;
    }

    public final void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraints = (BetaNodeFieldConstraint[])in.readObject();
        indexed = (boolean[]) in.readObject();
        indexPrecedenceOption = (IndexPrecedenceOption) in.readObject();
    }

    public final void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraints);
        out.writeObject(indexed);
        out.writeObject(indexPrecedenceOption);
    }

    public final void init(BuildContext context, short betaNodeType) {
        RuleBaseConfiguration config = context.getKnowledgeBase().getConfiguration();

        if ( disableIndexing || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory()) ) {
            indexed = new boolean[constraints.length];
        } else {
            int depth = config.getCompositeKeyDepth();
            if ( !compositeAllowed( constraints, betaNodeType ) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }
            initIndexes( depth, betaNodeType );
        }
    }

    public final void initIndexes(int depth, short betaNodeType) {
        indexed = isIndexableForNode(indexPrecedenceOption, betaNodeType, depth, constraints);
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
                                       final short nodeType) {
        return IndexUtil.Factory.createBetaMemory(config, nodeType, constraints);
    }

    public final BetaNodeFieldConstraint[] getConstraints() {
        return constraints;
    }

    public final ContextEntry[] createContext() {
        ContextEntry[] entries = new ContextEntry[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            entries[i] = constraints[i].createContextEntry();
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
        for (BetaNodeFieldConstraint constraint : constraints) {
            if ( !(constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).getConstraintType().isEquality()) ) {
                return false;
            }
        }
        return true;
    }
}
