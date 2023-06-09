package org.drools.base.rule.accessor;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.reteoo.BaseTuple;

public class RightTupleValueExtractor implements TupleValueExtractor {
    private ReadAccessor extractor;

    public RightTupleValueExtractor(ReadAccessor extractor) {
        this.extractor = extractor;
    }

    @Override
    public ValueType getValueType() {
        return extractor.getValueType();
    }

    @Override
    public Object getValue(ValueResolver valueResolver, BaseTuple tuple) {
        return extractor.getValue(valueResolver, tuple.getFactHandle().getObject());
    }

    @Override
    public TupleValueExtractor clone() {
        return new RightTupleValueExtractor(extractor);
    }
}
