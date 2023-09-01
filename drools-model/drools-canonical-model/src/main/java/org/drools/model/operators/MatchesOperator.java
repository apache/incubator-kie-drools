package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum MatchesOperator implements Operator.SingleValue<String, String> {

    INSTANCE;

    @Override
    public boolean eval( String s1, String s2 ) {
        return s1 != null && s1.matches( s2 );
    }

    @Override
    public String getOperatorName() {
        return "matches";
    }
}
