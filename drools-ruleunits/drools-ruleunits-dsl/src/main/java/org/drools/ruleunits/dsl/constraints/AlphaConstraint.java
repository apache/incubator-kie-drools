package org.drools.ruleunits.dsl.constraints;

import java.util.UUID;

import org.drools.model.AlphaIndex;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.alphaIndexedBy;

public class AlphaConstraint<L, R> implements Constraint<L> {
    private final Variable<L> variable;
    private final String fieldName;
    private final Function1<L, R> extractor;
    private final Index.ConstraintType constraintType;
    private final R rightValue;

    public AlphaConstraint(Variable<L> variable, String fieldName, Function1<L, R> extractor, Index.ConstraintType constraintType, R rightValue) {
        this.variable = variable;
        this.fieldName = fieldName;
        this.extractor = extractor;
        this.constraintType = constraintType;
        this.rightValue = rightValue;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId = fieldName != null ?
                "expr:" + variable.getType().getCanonicalName() + ":" + fieldName + ":" + constraintType + ":" + rightValue :
                UUID.randomUUID().toString();
        AlphaIndex alphaIndex = rightValue != null && fieldName != null ?
                alphaIndexedBy( (Class<R>) rightValue.getClass(), constraintType, ClassIntrospectionCache.getFieldIndex(variable.getType(), fieldName), extractor, rightValue ) :
                null;
        PatternDSL.ReactOn reactOn = fieldName != null ? PatternDSL.reactOn(fieldName) : null;
        patternDef.expr(exprId, p -> constraintType.asPredicate().test(extractor.apply(p), rightValue), alphaIndex, reactOn);
    }
}