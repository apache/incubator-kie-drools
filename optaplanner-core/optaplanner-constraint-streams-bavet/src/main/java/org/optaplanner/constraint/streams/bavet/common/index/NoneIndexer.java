package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.function.BiConsumer;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

final class NoneIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final NoneIndexerStorage<Tuple_, Value_> storage = new NoneIndexerStorage<>();

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Value_ old = storage.put(tuple, value);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") was already added in the indexer.");
        }
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Value_ value = storage.remove(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        return value;
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Value_ value = storage.get(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        return value;
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        storage.visit(tupleValueVisitor);
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

}
