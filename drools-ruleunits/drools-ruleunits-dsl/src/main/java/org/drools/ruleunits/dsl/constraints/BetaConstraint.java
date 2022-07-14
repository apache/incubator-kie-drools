package org.drools.ruleunits.dsl.constraints;

import java.util.UUID;

import org.drools.model.BetaIndex;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.betaIndexedBy;

public class BetaConstraint<L, R, V> implements Constraint<L> {
    private final Variable<L> leftVariable;
    private final String fieldName;
    private final Function1<L, V> leftExtractor;
    private final Index.ConstraintType constraintType;
    private final Variable<R> rightVariable;
    private final Function1<R, V> rightExtractor;

    public BetaConstraint(Variable<L> leftVariable, String fieldName, Function1<L, V> leftExtractor, Index.ConstraintType constraintType, Variable<R> rightVariable, Function1<R, V> rightExtractor) {
        this.leftVariable = leftVariable;
        this.fieldName = fieldName;
        this.leftExtractor = leftExtractor;
        this.constraintType = constraintType;
        this.rightVariable = rightVariable;
        this.rightExtractor = rightExtractor;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId = fieldName != null ?
                "expr:" + leftVariable.getType().getCanonicalName() + ":" + fieldName + ":" + constraintType :
                UUID.randomUUID().toString();
        BetaIndex betaIndex = fieldName != null ?
                betaIndexedBy( (Class<V>) Object.class, constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), fieldName), leftExtractor, rightExtractor ) :
                null;
        PatternDSL.ReactOn reactOn = fieldName != null ? PatternDSL.reactOn(fieldName) : null;
        patternDef.expr(exprId, rightVariable, (l, r) -> constraintType.asPredicate().test(leftExtractor.apply(l), rightExtractor.apply(r)), betaIndex, reactOn);
    }
}