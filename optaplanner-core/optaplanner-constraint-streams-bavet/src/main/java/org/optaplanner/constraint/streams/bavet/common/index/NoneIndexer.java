package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

final class NoneIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final Map<Tuple_, Value_> tupleMap = new LinkedHashMap<>();

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Value_ old = tupleMap.put(tuple, Objects.requireNonNull(value));
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") was already added in the indexer.");
        }
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Value_ value = tupleMap.remove(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        return value;
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Value_ value = tupleMap.get(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        return value;
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        tupleMap.forEach(tupleValueVisitor);
    }

    @Override
    public boolean isEmpty() {
        return tupleMap.isEmpty();
    }

}
