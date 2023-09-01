package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum StringLengthWithOperator implements Operator.SingleValue<String, Integer> {

    INSTANCE;

    @Override
    public boolean eval( String s1, Integer length ) {
       return s1.length() == length;
    }

    @Override
    public String getOperatorName() {
        return "str[length]";
    }
}
