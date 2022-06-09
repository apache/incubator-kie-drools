package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

final class EqualsIndexer<Tuple_ extends Tuple, Value_, Key_> implements Indexer<Tuple_, Value_> {

    private final Function<IndexProperties, Key_> indexerKeyFunction;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final Map<Key_, Indexer<Tuple_, Value_>> downstreamIndexerMap = new HashMap<>();

    public EqualsIndexer(Function<IndexProperties, Key_> indexerKeyFunction,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
        this.indexerKeyFunction = Objects.requireNonNull(indexerKeyFunction);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Indexer<Tuple_, Value_> downstreamIndexer =
                downstreamIndexerMap.computeIfAbsent(indexerKeyFunction.apply(indexProperties),
                        k -> downstreamIndexerSupplier.get());
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ oldIndexKey = indexerKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(oldIndexKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer" + this + ".");
        }
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            downstreamIndexerMap.remove(oldIndexKey);
        }
        return value;
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ oldIndexKey = indexerKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(oldIndexKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer" + this + ".");
        }
        return downstreamIndexer.get(indexProperties, tuple);
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexerKeyFunction.apply(indexProperties));
        if (downstreamIndexer == null || downstreamIndexer.isEmpty()) {
            return;
        }
        downstreamIndexer.visit(indexProperties, tupleValueVisitor);
    }

    @Override
    public boolean isEmpty() {
        return downstreamIndexerMap.isEmpty();
    }

}
