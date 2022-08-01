package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.core.impl.util.Pair;
import org.optaplanner.core.impl.util.Quadruple;
import org.optaplanner.core.impl.util.Triple;

final class EqualsIndexer<Tuple_ extends Tuple, Value_, Key_>
        implements Indexer<Tuple_, Value_> {

    private final Function<IndexProperties, Key_> indexKeyFunction;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final Map<Key_, Indexer<Tuple_, Value_>> downstreamIndexerMap = new HashMap<>();

    public EqualsIndexer(Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this(0, 1, downstreamIndexerSupplier);
    }

    public EqualsIndexer(int indexKeyFromInclusive, int indexKeyToExclusive,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.indexKeyFunction = getIndexKeyFunction(indexKeyFromInclusive, indexKeyToExclusive);
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        Key_ indexKey = indexKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null || downstreamIndexer.isEmpty()) {
            return;
        }
        downstreamIndexer.visit(indexProperties, tupleValueVisitor);
    }

    private Function<IndexProperties, Key_> getIndexKeyFunction(int indexKeyFromInclusive, int indexKeyToExclusive) {
        int length = indexKeyToExclusive - indexKeyFromInclusive;
        switch (length) {
            case 1:
                return indexProperties -> indexProperties.getProperty(indexKeyFromInclusive);
            case 2:
                return indexProperties -> (Key_) Pair.of(indexProperties.getProperty(indexKeyFromInclusive),
                        indexProperties.getProperty(indexKeyFromInclusive + 1));
            case 3:
                return indexProperties -> (Key_) Triple.of(indexProperties.getProperty(indexKeyFromInclusive),
                        indexProperties.getProperty(indexKeyFromInclusive + 1),
                        indexProperties.getProperty(indexKeyFromInclusive + 2));
            case 4:
                return indexProperties -> (Key_) Quadruple.of(indexProperties.getProperty(indexKeyFromInclusive),
                        indexProperties.getProperty(indexKeyFromInclusive + 1),
                        indexProperties.getProperty(indexKeyFromInclusive + 2),
                        indexProperties.getProperty(indexKeyFromInclusive + 3));
            default:
                return indexProperties -> (Key_) new IndexerKey(indexProperties, indexKeyFromInclusive, indexKeyToExclusive);
        }
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ indexKey = indexKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, tuple);
        return downstreamIndexer.get(indexProperties, tuple);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Key_ indexKey = indexKeyFunction.apply(indexProperties);
        // Avoids computeIfAbsent in order to not create lambdas on the hot path.
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexKey);
        if (downstreamIndexer == null) {
            downstreamIndexer = downstreamIndexerSupplier.get();
            downstreamIndexerMap.put(indexKey, downstreamIndexer);
        }
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ indexKey = indexKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, tuple);
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            downstreamIndexerMap.remove(indexKey);
        }
        return value;
    }

    private Indexer<Tuple_, Value_> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey, Tuple_ tuple) {
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer " + this + ".");
        }
        return downstreamIndexer;
    }

    @Override
    public boolean isEmpty() {
        return downstreamIndexerMap.isEmpty();
    }
}
