package org.drools.mvel.evaluators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ValueType;
import org.drools.drl.parser.impl.Operator;
import org.drools.base.rule.accessor.Evaluator;
import org.drools.base.time.Interval;

/**
 * BaseEvaluator is an Object Comparator that is operator aware
 */
public abstract class BaseEvaluator implements MvelEvaluator, Externalizable {

    private Operator  operator;

    private ValueType type;

    public BaseEvaluator() {
    }

    public BaseEvaluator(final ValueType type,
                         final Operator operator) {
        this.type = type;
        this.operator = operator;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        operator    = (Operator)in.readObject();
        type        = (ValueType)in.readObject();
        if (type != null) {
            type   = ValueType.determineValueType(type.getClassType());
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(operator);
        out.writeObject(type);
    }

    public Operator getOperator() {
        return this.operator;
    }

    public ValueType getValueType() {
        return this.type;
    }

    public ValueType getCoercedValueType() {
        return this.type;
    }
    
    public boolean isTemporal() {
        return false;
    }
    
    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if (!(object instanceof BaseEvaluator)) {
            return false;
        }

        final Evaluator other = (Evaluator) object;
        return this.getOperator() == other.getOperator() && this.getValueType() == other.getValueType();
    }

    public int hashCode() {
        return (this.getValueType().hashCode()) ^ (this.getOperator().hashCode()) ^ (this.getClass().hashCode());
    }

    public String toString() {
        return type.getName()+" "+operator.toString();
    }
    
}
