package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;

final class EqualsIndexer<T, Key_> implements Indexer<T> {

    private final int indexKeyFrom;
    private final int indexKeyTo;
    private final Supplier<Indexer<T>> downstreamIndexerSupplier;
    private final Map<Key_, Indexer<T>> downstreamIndexerMap = new HashMap<>();

    public EqualsIndexer(Supplier<Indexer<T>> downstreamIndexerSupplier) {
        this(0, 1, downstreamIndexerSupplier);
    }

    public EqualsIndexer(int indexKeyFromInclusive, int indexKeyToExclusive,
            Supplier<Indexer<T>> downstreamIndexerSupplier) {
        this.indexKeyFrom = indexKeyFromInclusive;
        this.indexKeyTo = indexKeyToExclusive;
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
    }

    @Override
    public TupleListEntry<T> put(IndexProperties indexProperties, T tuple) {
        Key_ indexKey = indexProperties.toKey(indexKeyFrom, indexKeyTo);
        // Avoids computeIfAbsent in order to not create lambdas on the hot path.
        Indexer<T> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null) {
            downstreamIndexer = downstreamIndexerSupplier.get();
            downstreamIndexerMap.put(indexKey, downstreamIndexer);
        }
        return downstreamIndexer.put(indexProperties, tuple);
    }

    @Override
    public void remove(IndexProperties indexProperties, TupleListEntry<T> entry) {
        Key_ indexKey = indexProperties.toKey(indexKeyFrom, indexKeyTo);
        Indexer<T> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, entry);
        downstreamIndexer.remove(indexProperties, entry);
        if (downstreamIndexer.isEmpty()) {
            downstreamIndexerMap.remove(indexKey);
        }
    }

    private Indexer<T> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey,
            TupleListEntry<T> entry) {
        Indexer<T> downstreamIndexer = downstreamIndexerMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + entry.getElement()
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer " + this + ".");
        }
        return downstreamIndexer;
    }

    @Override
    public int size(IndexProperties indexProperties) {
        Key_ indexKey = indexProperties.toKey(indexKeyFrom, indexKeyTo);
        Indexer<T> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null) {
            return 0;
        }
        return downstreamIndexer.size(indexProperties);
    }

    @Override
    public void forEach(IndexProperties indexProperties, Consumer<T> tupleConsumer) {
        Key_ indexKey = indexProperties.toKey(indexKeyFrom, indexKeyTo);
        Indexer<T> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null || downstreamIndexer.isEmpty()) {
            return;
        }
        downstreamIndexer.forEach(indexProperties, tupleConsumer);
    }

    @Override
    public boolean isEmpty() {
        return downstreamIndexerMap.isEmpty();
    }

    @Override
    public String toString() {
        return "size = " + downstreamIndexerMap.size();
    }

}
