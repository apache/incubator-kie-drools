package org.drools.ruleunits.dsl.constraints;

import java.util.UUID;

import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public class AlphaConstraintWithRightExtractor<L, R> extends AbstractConstraint<L, R> {

    private final String rightFieldName;
    private final Function1<L, R> rightExtractor;

    public AlphaConstraintWithRightExtractor(Variable<L> variable, String fieldName, Function1<L, R> leftExtractor, Index.ConstraintType constraintType, String rightFieldName, Function1<L, R> rightExtractor) {
        super(variable, fieldName, leftExtractor, constraintType);
        this.rightFieldName = rightFieldName;
        this.rightExtractor = rightExtractor;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId;
        PatternDSL.ReactOn reactOn = null;
        if (leftFieldName != null) {
            exprId = rightFieldName != null ?
                    "expr:" + leftVariable.getType().getCanonicalName() + ":" + leftFieldName + ":" + constraintType + ":" + rightFieldName :
                    UUID.randomUUID().toString();
            reactOn = rightFieldName != null ?
                    PatternDSL.reactOn(leftFieldName, rightFieldName) :
                    PatternDSL.reactOn(leftFieldName);
        } else {
            exprId = UUID.randomUUID().toString();
        }
        patternDef.expr(exprId, p -> constraintType.asPredicate().test(leftExtractor.apply(p), rightExtractor.apply(p)), null, reactOn);
    }
}