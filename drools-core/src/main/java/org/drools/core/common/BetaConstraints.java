package org.drools.core.common;

import java.io.Externalizable;
import java.util.List;

import org.drools.base.base.ObjectType;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface BetaConstraints
    extends
    Externalizable {

    ContextEntry[] createContext();

    void updateFromTuple(ContextEntry[] context,
                         ReteEvaluator reteEvaluator,
                         Tuple tuple);

    void updateFromFactHandle(ContextEntry[] context,
                              ReteEvaluator reteEvaluator,
                              FactHandle handle);

    boolean isAllowedCachedLeft(ContextEntry[] context,
                                FactHandle handle);

    boolean isAllowedCachedRight(ContextEntry[] context,
                                 Tuple tuple);

    BetaNodeFieldConstraint[] getConstraints();

    BetaConstraints getOriginalConstraint();
    
    boolean isIndexed();

    int getIndexCount();

    boolean isEmpty();

    BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                final short nodeType );

    void resetTuple(final ContextEntry[] context);

    void resetFactHandle(final ContextEntry[] context);

    BitMask getListenedPropertyMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties);

    void init(BuildContext context, short betaNodeType);
    void initIndexes(int depth, short betaNodeType, RuleBaseConfiguration config);

    BetaConstraints cloneIfInUse();

    boolean isLeftUpdateOptimizationAllowed();

    void registerEvaluationContext(BuildContext buildContext);
}
