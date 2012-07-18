package org.drools.common;

import org.drools.RuleBaseConfiguration;
import org.drools.conf.IndexPrecedenceOption;
import org.drools.core.util.index.IndexUtil;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.BetaNodeFieldConstraint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.drools.core.util.index.IndexUtil.compositeAllowed;
import static org.drools.core.util.index.IndexUtil.isIndexableForNode;

public abstract class MultipleBetaConstraint implements BetaConstraints {
    protected BetaNodeFieldConstraint[] constraints;
    protected boolean[]                 indexed;
    private IndexPrecedenceOption       indexPrecedenceOption;
    private transient boolean           disableIndexing;

    public MultipleBetaConstraint() { }

    public MultipleBetaConstraint( BetaNodeFieldConstraint[] constraints,
                                   RuleBaseConfiguration conf,
                                   boolean disableIndexing) {
        this.constraints = constraints;
        this.disableIndexing = disableIndexing;
        this.indexPrecedenceOption = conf.getIndexPrecedenceOption();
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

    public final void init(BuildContext context, BetaNode betaNode) {
        RuleBaseConfiguration config = context.getRuleBase().getConfiguration();

        if ( disableIndexing || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory()) ) {
            indexed = new boolean[constraints.length];
        } else {
            int depth = config.getCompositeKeyDepth();
            if ( !compositeAllowed( constraints, betaNode.getType() ) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }
            initIndexes( depth, betaNode.getType() );
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
}
