package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum ExcludesOperator implements Operator.SingleValue<Object, Object> {

    INSTANCE;

    @Override
    public boolean eval( Object a, Object b ) {
        return !ContainsOperator.INSTANCE.eval( a, b );
    }

    @Override
    public String getOperatorName() {
        return "excludes";
    }
}
