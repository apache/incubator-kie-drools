package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.Declaration;

final class ValueExtractor<X> implements Function<Tuple, X> {

    private static final UnaryOperator<Tuple> TUPLE_EXTRACTOR_OFFSET_1 = Tuple::getParent;
    private static final UnaryOperator<Tuple> TUPLE_EXTRACTOR_OFFSET_2 = tuple -> tuple.getParent()
            .getParent();
    private static final UnaryOperator<Tuple> TUPLE_EXTRACTOR_OFFSET_3 = tuple -> tuple.getParent()
            .getParent()
            .getParent();

    static UnaryOperator<Tuple> getTupleExtractor(Declaration declaration, Tuple leftTuple) {
        int offset = 0;
        while (leftTuple.getIndex() != declaration.getTupleIndex()) {
            leftTuple = leftTuple.getParent();
            offset++;
        }
        switch (offset) {
            case 0: // The tuple will be accessed directly to avoid a method call on the hot path.
                return null;
            case 1:
                return TUPLE_EXTRACTOR_OFFSET_1;
            case 2:
                return TUPLE_EXTRACTOR_OFFSET_2;
            case 3:
                return TUPLE_EXTRACTOR_OFFSET_3;
            default:
                throw new UnsupportedOperationException("Impossible state: tuple delta offset (" + offset + ").");
        }
    }

    private final Declaration declaration;
    private final UnaryOperator<Tuple> tupleExtractor;

    public ValueExtractor(Declaration declaration, Tuple leftTuple) {
        this.declaration = Objects.requireNonNull(declaration);
        this.tupleExtractor = getTupleExtractor(declaration, leftTuple);
    }

    @Override
    public X apply(Tuple tuple) {
        Tuple extractedTuple = tupleExtractor == null ? tuple : tupleExtractor.apply(tuple);
        return (X) declaration.getValue(null, extractedTuple.getFactHandle().getObject());
    }
}
