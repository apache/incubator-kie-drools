package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

final class EqualsIndexer<Tuple_ extends Tuple, Value_, Key_>
        implements Indexer<Tuple_, Value_> {

    private final int indexKeyFromInclusive;
    private final int indexKeyToExclusive;
    private final Function<Key_, Indexer<Tuple_, Value_>> downstreamIndexerFunction;
    private final Map<Key_, Indexer<Tuple_, Value_>> downstreamIndexerMap = new HashMap<>();

    public EqualsIndexer(Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this(0, 1, downstreamIndexerSupplier);
    }

    public EqualsIndexer(int indexKeyFromInclusive, int indexKeyToExclusive,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.indexKeyFromInclusive = indexKeyFromInclusive;
        this.indexKeyToExclusive = indexKeyToExclusive;
        // Avoid creating the capturing lambda over and over on the hot path.
        this.downstreamIndexerFunction = k -> downstreamIndexerSupplier.get();
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        Key_ indexKey = getIndexerKey(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null || downstreamIndexer.isEmpty()) {
            return;
        }
        downstreamIndexer.visit(indexProperties, tupleValueVisitor);
    }

    private Key_ getIndexerKey(IndexProperties indexProperties) {
        return indexProperties.getIndexerKey(indexKeyFromInclusive, indexKeyToExclusive);
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ indexerKey = getIndexerKey(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, indexerKey, tuple);
        return downstreamIndexer.get(indexProperties, tuple);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Key_ indexerKey = getIndexerKey(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer =
                downstreamIndexerMap.computeIfAbsent(indexerKey, downstreamIndexerFunction);
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ oldIndexerKey = getIndexerKey(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, oldIndexerKey, tuple);
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            downstreamIndexerMap.remove(oldIndexerKey);
        }
        return value;
    }

    private Indexer<Tuple_, Value_> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey, Tuple_ tuple) {
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer" + this + ".");
        }
        return downstreamIndexer;
    }

    @Override
    public boolean isEmpty() {
        return downstreamIndexerMap.isEmpty();
    }
}
