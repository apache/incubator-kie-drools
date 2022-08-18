package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.core.impl.score.stream.JoinerType;

final class ComparisonIndexer<Tuple_ extends Tuple, Value_, Key_ extends Comparable<Key_>>
        implements Indexer<Tuple_, Value_> {

    private final int indexKeyPosition;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final Comparator<Key_> keyComparator;
    private final boolean hasOrEquals;
    private final NavigableMap<Key_, Indexer<Tuple_, Value_>> comparisonMap;

    public ComparisonIndexer(JoinerType comparisonJoinerType, Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this(comparisonJoinerType, 0, downstreamIndexerSupplier);
    }

    public ComparisonIndexer(JoinerType comparisonJoinerType, int indexKeyPosition,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.indexKeyPosition = indexKeyPosition;
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
        /*
         * For GT/GTE, the iteration order is reversed.
         * This allows us to iterate over the entire map, stopping when the threshold is reached.
         * This is done so that we can avoid using head/tail sub maps, which are expensive.
         */
        this.keyComparator =
                (comparisonJoinerType == JoinerType.GREATER_THAN || comparisonJoinerType == JoinerType.GREATER_THAN_OR_EQUAL)
                        ? KeyComparator.INSTANCE.reversed()
                        : KeyComparator.INSTANCE;
        this.hasOrEquals = comparisonJoinerType == JoinerType.GREATER_THAN_OR_EQUAL
                || comparisonJoinerType == JoinerType.LESS_THAN_OR_EQUAL;
        this.comparisonMap = new TreeMap<>(keyComparator);
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        int size = comparisonMap.size();
        if (size == 0) {
            return;
        }
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        if (size == 1) { // Avoid creation of the entry set and iterator.
            Map.Entry<Key_, Indexer<Tuple_, Value_>> entry = comparisonMap.firstEntry();
            visitEntry(indexProperties, tupleValueVisitor, indexKey, entry);
        } else {
            for (Map.Entry<Key_, Indexer<Tuple_, Value_>> entry : comparisonMap.entrySet()) {
                boolean boundaryReached = visitEntry(indexProperties, tupleValueVisitor, indexKey, entry);
                if (boundaryReached) {
                    return;
                }
            }
        }
    }

    private boolean visitEntry(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor,
            Key_ indexKey, Map.Entry<Key_, Indexer<Tuple_, Value_>> entry) {
        // Comparator matches the order of iteration of the map, so the boundary is always found from the bottom up.
        int comparison = keyComparator.compare(entry.getKey(), indexKey);
        if (comparison >= 0) { // Possibility of reaching the boundary condition.
            if (comparison > 0 || !hasOrEquals) {
                // Boundary condition reached when we're out of bounds entirely, or when GTE/LTE is not allowed.
                return true;
            }
        }
        // Boundary condition not yet reached; include the indexer in the range.
        entry.getValue().visit(indexProperties, tupleValueVisitor);
        return false;
    }

    @Override
    public Value_ get(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, tuple);
        return downstreamIndexer.get(indexProperties, tuple);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        // Avoids computeIfAbsent in order to not create lambdas on the hot path.
        Indexer<Tuple_, Value_> downstreamIndexer = comparisonMap.get(indexKey);
        if (downstreamIndexer == null) {
            downstreamIndexer = downstreamIndexerSupplier.get();
            comparisonMap.put(indexKey, downstreamIndexer);
        }
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, tuple);
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            comparisonMap.remove(indexKey);
        }
        return value;
    }

    private Indexer<Tuple_, Value_> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey, Tuple_ tuple) {
        Indexer<Tuple_, Value_> downstreamIndexer = comparisonMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer " + this + ".");
        }
        return downstreamIndexer;
    }

    @Override
    public boolean isEmpty() {
        return comparisonMap.isEmpty();
    }

    private static final class KeyComparator<Key_ extends Comparable<Key_>> implements Comparator<Key_> {

        private static final Comparator INSTANCE = new KeyComparator<>();

        @Override
        public int compare(Key_ o1, Key_ o2) {
            if (o1 == o2) {
                return 0;
            }
            return o1.compareTo(o2);
        }

    }

}