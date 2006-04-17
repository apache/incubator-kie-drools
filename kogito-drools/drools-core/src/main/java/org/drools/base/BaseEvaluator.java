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

    private final int operator;

    private final int type;

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

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !this.getClass().equals( other.getClass() ) ) {
            return false;
        }
        return (this.getOperator() == ((Evaluator) other).getOperator()) && (this.getType() == ((Evaluator) other).getType());
    }

    public int hashCode() {
        return (this.getType() * 17) ^ (this.getOperator() * 11) ^ (this.getClass().hashCode());
    }

}
