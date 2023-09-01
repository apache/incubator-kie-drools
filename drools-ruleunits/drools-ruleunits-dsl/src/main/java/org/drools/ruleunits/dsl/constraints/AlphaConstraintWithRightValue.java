package org.drools.ruleunits.dsl.constraints;

import java.util.UUID;

import org.drools.model.AlphaIndex;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.alphaIndexedBy;

public class AlphaConstraintWithRightValue<L, R> extends AbstractConstraint<L, R> {

    private final R rightValue;

    public AlphaConstraintWithRightValue(Variable<L> variable, String fieldName, Function1<L, R> extractor, Index.ConstraintType constraintType, R rightValue) {
        super(variable, fieldName, extractor, constraintType);
        this.rightValue = rightValue;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId;
        AlphaIndex alphaIndex = null;
        PatternDSL.ReactOn reactOn = null;
        if (leftFieldName != null) {
            exprId = "expr:" + leftVariable.getType().getCanonicalName() + ":" + leftFieldName + ":" + constraintType + ":" + rightValue;
            alphaIndex = rightValue != null ?
                    alphaIndexedBy( (Class<R>) rightValue.getClass(), constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), leftFieldName), leftExtractor, rightValue ) :
                    null;
            reactOn = PatternDSL.reactOn(leftFieldName);
        } else {
            exprId = UUID.randomUUID().toString();
        }
        patternDef.expr(exprId, p -> constraintType.asPredicate().test(leftExtractor.apply(p), rightValue), alphaIndex, reactOn);
    }
}