package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum MemberOfOperator implements Operator.SingleValue<Object, Object> {

    INSTANCE;

    @Override
    public boolean eval( Object a, Object b ) {
        return ContainsOperator.INSTANCE.eval( b, a );
    }

    @Override
    public String getOperatorName() {
        return "memberOf";
    }
}
