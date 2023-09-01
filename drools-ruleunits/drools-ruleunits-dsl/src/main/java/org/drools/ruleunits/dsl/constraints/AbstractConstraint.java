package org.drools.ruleunits.dsl.constraints;

import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public abstract class AbstractConstraint<L, R> implements Constraint<L> {

    protected final Variable<L> leftVariable;
    protected final String leftFieldName;
    protected final Function1<L, R> leftExtractor;
    protected final Index.ConstraintType constraintType;

    public AbstractConstraint(Variable<L> leftVariable, String leftFieldName, Function1<L, R> leftExtractor, Index.ConstraintType constraintType) {
        this.leftVariable = leftVariable;
        this.leftFieldName = leftFieldName;
        this.leftExtractor = leftExtractor;
        this.constraintType = constraintType;
    }
}