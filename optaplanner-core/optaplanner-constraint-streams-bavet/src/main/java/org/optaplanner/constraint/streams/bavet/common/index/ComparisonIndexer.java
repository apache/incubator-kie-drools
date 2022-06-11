package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.core.impl.score.stream.JoinerType;

final class ComparisonIndexer<Tuple_ extends Tuple, Value_, Key_ extends Comparable<Key_>>
        implements Indexer<Tuple_, Value_> {

    private final int indexKeyPosition;
    private final Function<Key_, Map<Key_, Indexer<Tuple_, Value_>>> submapExtractor;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final NavigableMap<Key_, Indexer<Tuple_, Value_>> comparisonMap = new TreeMap<>(new KeyComparator<>());

    public ComparisonIndexer(JoinerType comparisonJoinerType, Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this(comparisonJoinerType, 0, downstreamIndexerSupplier);
    }

    public ComparisonIndexer(JoinerType comparisonJoinerType, int indexKeyPosition,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.indexKeyPosition = indexKeyPosition;
        this.submapExtractor = getSubmapExtractor(comparisonJoinerType);
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
    }

    private Function<Key_, Map<Key_, Indexer<Tuple_, Value_>>> getSubmapExtractor(JoinerType comparisonJoinerType) {
        switch (comparisonJoinerType) {
            case LESS_THAN:
                return this::getLessThanSubmapExtractor;
            case LESS_THAN_OR_EQUAL:
                return this::getLessThanOrEqualSubmapExtractor;
            case GREATER_THAN:
                return this::getGreaterThanSubmapExtractor;
            case GREATER_THAN_OR_EQUAL:
                return this::getGreaterThanOrEqualSubmapExtractor;
            default:
                throw new IllegalStateException("Impossible state: the comparisonJoinerType (" + comparisonJoinerType
                        + ") is not one of the 4 comparison types.");
        }
    }

    private Map<Key_, Indexer<Tuple_, Value_>> getLessThanSubmapExtractor(Key_ comparisonIndexProperty) {
        return comparisonMap.headMap(comparisonIndexProperty, false);
    }

    private Map<Key_, Indexer<Tuple_, Value_>> getLessThanOrEqualSubmapExtractor(Key_ comparisonIndexProperty) {
        return comparisonMap.headMap(comparisonIndexProperty, true);
    }

    private Map<Key_, Indexer<Tuple_, Value_>> getGreaterThanSubmapExtractor(Key_ comparisonIndexProperty) {
        return comparisonMap.tailMap(comparisonIndexProperty, false);
    }

    private Map<Key_, Indexer<Tuple_, Value_>> getGreaterThanOrEqualSubmapExtractor(Key_ comparisonIndexProperty) {
        return comparisonMap.tailMap(comparisonIndexProperty, true);
    }

    @Override
    public void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueVisitor) {
        Key_ comparisonIndexProperty = getIndexerKey(indexProperties);
        Map<Key_, Indexer<Tuple_, Value_>> selectedComparisonMap =
                submapExtractor.apply(comparisonIndexProperty);
        for (Indexer<Tuple_, Value_> indexer : selectedComparisonMap.values()) {
            indexer.visit(indexProperties, tupleValueVisitor);
        }
    }

    private Key_ getIndexerKey(IndexProperties indexProperties) {
        return indexProperties.getProperty(indexKeyPosition);
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
                comparisonMap.computeIfAbsent(indexerKey, k -> downstreamIndexerSupplier.get());
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Key_ oldIndexerKey = getIndexerKey(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = getDownstreamIndexer(indexProperties, oldIndexerKey, tuple);
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            comparisonMap.remove(oldIndexerKey);
        }
        return value;
    }

    private Indexer<Tuple_, Value_> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey, Tuple_ tuple) {
        Indexer<Tuple_, Value_> downstreamIndexer = comparisonMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer" + this + ".");
        }
        return downstreamIndexer;
    }

    @Override
    public boolean isEmpty() {
        return comparisonMap.isEmpty();
    }

    private static final class KeyComparator<Key_ extends Comparable<Key_>> implements Comparator<Key_> {

        @Override
        public int compare(Key_ o1, Key_ o2) { // Exists so that the comparison operations can be more easily debugged.
            return o1.compareTo(o2);
        }

    }

}