package org.drools.base.rule.accessor;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.reteoo.BaseTuple;

public interface TupleValueExtractor extends Cloneable {

    ValueType getValueType();

    default Object getValue( BaseTuple tuple) {
        return getValue( null, tuple );
    }

    Object getValue(ValueResolver valueResolver, BaseTuple tuple);

    TupleValueExtractor clone();
}
