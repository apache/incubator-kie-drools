package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum StringStartsWithOperator implements Operator.SingleValue<String, String> {

    INSTANCE;

    @Override
    public boolean eval( String s1, String s2 ) {
       return s1.startsWith(s2);
    }

    @Override
    public String getOperatorName() {
        return "str[startsWith]";
    }
}
