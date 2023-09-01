package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum InOperator implements Operator.MultipleValue<Object, Object> {

    INSTANCE;

    @Override
    public boolean eval( Object a, Object[] bs ) {
        for (Object b : bs) {
            if (a == null && b == null) {
                return true;
            } else if (a != null && a.equals( b )) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getOperatorName() {
        return "in";
    }

    @Override
    public boolean requiresCoercion() {
        return true;
    }
}
