package org.drools.base;

import org.drools.spi.Evaluator;

/**
 * BaseEvaluator is an Object Comparator that is operator aware
 * 
 * @author mproctor
 * 
 */
public abstract class BaseEvaluator
    implements
    Evaluator {

    private final int       operator;

    private final int       type;

    public BaseEvaluator(int type,
                         int operator) {
        this.type = type;
        this.operator = operator;
    }

    public int getOperator() {
        return this.operator;
    }

    public int getType() {
        return this.type;
    }

    public abstract boolean evaluate(Object object1,
                                     Object object2);

}
