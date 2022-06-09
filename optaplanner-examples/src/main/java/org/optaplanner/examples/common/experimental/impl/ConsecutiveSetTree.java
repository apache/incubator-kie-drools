package org.optaplanner.examples.common.experimental.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.Sequence;

/**
 * A {@code ConsecutiveSetTree} determines what values are consecutive. A sequence
 * <i>x<sub>1</sub>,&nbsp;x<sub>2</sub>,&nbsp;x<sub>3</sub>,&nbsp;...,&nbsp;x<sub>n</sub></i>
 * is understood to be consecutive by <i>d</i> iff
 * <i>x<sub>2</sub> &minus; x<sub>1</sub> &le; d, x<sub>3</sub> &minus; x<sub>2</sub> &le; d, ..., x<sub>n</sub> &minus;
 * x<sub>n-1</sub> &le; d</i>.
 * This data structure can be thought as an interval tree that maps the point <i>p</i> to
 * the interval <i>[p, p + d]</i>.
 *
 * @param <Value_> The type of value stored (examples: shifts)
 * @param <Point_> The type of the point (examples: int, LocalDateTime)
 * @param <Difference_> The type of the difference (examples: int, Duration)
 */
public final class ConsecutiveSetTree<Value_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        implements ConsecutiveInfo<Value_, Difference_> {
    private final BiFunction<Point_, Point_, Difference_> differenceFunction;
    private final BiFunction<Difference_, Difference_, Difference_> sumFunction;
    private final Difference_ maxDifference;
    private final Difference_ zeroDifference;
    private final NavigableMap<Value_, Integer> itemToCountMap;
    private final NavigableMap<Value_, SequenceImpl<Value_, Difference_>> startItemToSequence;
    private final NavigableMap<Value_, BreakImpl<Value_, Difference_>> startItemToPreviousBreak;

    private final Map<Value_, Point_> indexMap;
    private final MapValuesIterable<Value_, SequenceImpl<Value_, Difference_>> sequenceList;
    private final MapValuesIterable<Value_, BreakImpl<Value_, Difference_>> breakList;

    public ConsecutiveSetTree(BiFunction<Point_, Point_, Difference_> differenceFunction,
            BiFunction<Difference_, Difference_, Difference_> sumFunction,
            Difference_ maxDifference,
            Difference_ zeroDifference) {
        this.differenceFunction = differenceFunction;
        this.sumFunction = sumFunction;
        this.maxDifference = maxDifference;
        this.zeroDifference = zeroDifference;
        indexMap = new HashMap<>();
        Comparator<Value_> comparator = new ValueComparator<>(indexMap);
        itemToCountMap = new TreeMap<>(comparator);
        startItemToSequence = new TreeMap<>(comparator);
        startItemToPreviousBreak = new TreeMap<>(comparator);
        sequenceList = new MapValuesIterable<>(startItemToSequence);
        breakList = new MapValuesIterable<>(startItemToPreviousBreak);
    }

    // Public API
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterable<Sequence<Value_, Difference_>> getConsecutiveSequences() {
        return (Iterable) sequenceList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterable<Break<Value_, Difference_>> getBreaks() {
        return (Iterable) breakList;
    }

    public boolean add(Value_ item, Point_ point) {
        indexMap.put(item, point);
        int newCount = itemToCountMap.compute(item, (key, count) -> count == null ? 1 : count + 1);
        if (newCount > 1) { // Item already in bag.
            return true;
        }
        Value_ firstBeforeItem = startItemToSequence.floorKey(item);
        Point_ itemIndex = indexMap.get(item);
        if (firstBeforeItem != null) {
            Value_ endOfBeforeSequenceItem = getEndItem(firstBeforeItem);
            Point_ endOfBeforeSequenceIndex = indexMap.get(endOfBeforeSequenceItem);
            if (isInNaturalOrderAndHashOrderIfEqual(itemIndex, item, endOfBeforeSequenceIndex, endOfBeforeSequenceItem)) {
                // Item is already in the bag; do nothing
                return true;
            } else {
                // Item is outside the bag
                Value_ firstAfterItem = startItemToSequence.higherKey(item);
                if (firstAfterItem != null) {
                    Point_ startOfAfterSequenceIndex = indexMap.get(firstAfterItem);
                    addBetweenItems(item, itemIndex, firstBeforeItem, endOfBeforeSequenceItem,
                            endOfBeforeSequenceIndex, firstAfterItem, startOfAfterSequenceIndex);
                } else {
                    SequenceImpl<Value_, Difference_> prevBag = startItemToSequence.get(firstBeforeItem);
                    if (isFirstSuccessorOfSecond(itemIndex, item, endOfBeforeSequenceIndex, endOfBeforeSequenceItem)) {
                        // We need to extend the first bag
                        // No break since afterItem is null
                        prevBag.setEnd(item);
                    } else {
                        // Start a new bag of consecutive items
                        SequenceImpl<Value_, Difference_> newBag = new SequenceImpl<>(this, item);
                        startItemToSequence.put(item, newBag);
                        startItemToPreviousBreak.put(item,
                                new BreakImpl<>(prevBag, newBag,
                                        differenceFunction.apply(endOfBeforeSequenceIndex, itemIndex)));
                    }
                }
            }
        } else {
            // No items before it
            Value_ firstAfterItem = startItemToSequence.higherKey(item);
            if (firstAfterItem != null) {
                Point_ startOfAfterSequenceIndex = indexMap.get(firstAfterItem);

                if (isFirstSuccessorOfSecond(startOfAfterSequenceIndex, firstAfterItem, itemIndex, item)) {
                    // We need to move the after bag to use item as key
                    SequenceImpl<Value_, Difference_> afterBag = startItemToSequence.remove(firstAfterItem);
                    afterBag.setStart(item);
                    // No break since this is the first sequence
                    startItemToSequence.put(item, afterBag);
                } else {
                    // Start a new bag of consecutive items
                    SequenceImpl<Value_, Difference_> afterBag = startItemToSequence.get(firstAfterItem);
                    SequenceImpl<Value_, Difference_> newBag = new SequenceImpl<>(this, item);
                    startItemToSequence.put(item, newBag);
                    startItemToPreviousBreak.put(firstAfterItem,
                            new BreakImpl<>(newBag, afterBag,
                                    differenceFunction.apply(itemIndex, startOfAfterSequenceIndex)));
                }
            } else {
                // Start a new bag of consecutive items
                SequenceImpl<Value_, Difference_> newBag = new SequenceImpl<>(this, item);
                startItemToSequence.put(item, newBag);
                // Bag have no other items, so no break
            }
        }
        return true;
    }

    public boolean remove(Value_ item) {
        Integer currentCount = itemToCountMap.get(item);
        if (currentCount == null) { // Item not in bag.
            return false;
        }
        if (currentCount == 1) {
            itemToCountMap.remove(item);
        } else { // Item still in bag.
            itemToCountMap.put(item, currentCount - 1);
            return true;
        }

        // Item is removed from bag
        Value_ firstBeforeItem = startItemToSequence.floorKey(item);
        SequenceImpl<Value_, Difference_> bag = startItemToSequence.get(firstBeforeItem);
        Value_ endItem = bag.getLastItem();

        // Bag is empty if first item = last item
        if (bag.getFirstItem() == bag.getLastItem()) {
            startItemToSequence.remove(firstBeforeItem);
            BreakImpl<Value_, Difference_> removedBreak = startItemToPreviousBreak.remove(firstBeforeItem);
            Map.Entry<Value_, BreakImpl<Value_, Difference_>> extendedBreakEntry =
                    startItemToPreviousBreak.higherEntry(firstBeforeItem);
            if (extendedBreakEntry != null) {
                if (removedBreak != null) {
                    BreakImpl<Value_, Difference_> extendedBreak = extendedBreakEntry.getValue();
                    extendedBreak.setPreviousSequence(removedBreak.getPreviousSequence());
                    updateLengthOfBreak(extendedBreak);
                } else {
                    startItemToPreviousBreak.remove(extendedBreakEntry.getKey());
                }
            }
            indexMap.remove(item);
            return true;
        }

        // Bag is not empty
        return removeItemFromBag(bag, item, firstBeforeItem, endItem);
    }

    // Protected API
    Break<Value_, Difference_> getBreakBefore(Value_ item) {
        return startItemToPreviousBreak.get(item);
    }

    Break<Value_, Difference_> getBreakAfter(Value_ item) {
        Map.Entry<Value_, BreakImpl<Value_, Difference_>> entry = startItemToPreviousBreak.higherEntry(item);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    NavigableSet<Value_> getItemSet() {
        return (NavigableSet<Value_>) itemToCountMap.keySet();
    }

    void updateLengthOfBreak(BreakImpl<Value_, Difference_> theBreak) {
        theBreak.setLength(getBreakLengthBetween(theBreak.getPreviousSequenceEnd(), theBreak.getNextSequenceStart()));
    }

    Difference_ getSequenceLength(Sequence<Value_, Difference_> sequence) {
        return sumFunction.apply(maxDifference, differenceFunction.apply(indexMap.get(sequence.getFirstItem()),
                indexMap.get(sequence.getLastItem())));
    }

    Difference_ getBreakLengthBetween(Value_ from, Value_ to) {
        return differenceFunction.apply(indexMap.get(from), indexMap.get(to));
    }

    Value_ getEndItem(Value_ key) {
        return startItemToSequence.get(key).getLastItem();
    }

    private <T extends Comparable<T>> boolean isInNaturalOrderAndHashOrderIfEqual(T a, Value_ aItem, T b,
            Value_ bItem) {
        int difference = a.compareTo(b);
        if (difference != 0) {
            return difference < 0;
        }
        return System.identityHashCode(aItem) - System.identityHashCode(bItem) < 0;
    }

    private boolean isFirstSuccessorOfSecond(Point_ first, Value_ firstValue, Point_ second,
            Value_ secondValue) {
        Difference_ difference = differenceFunction.apply(second, first);
        return isInNaturalOrderAndHashOrderIfEqual(zeroDifference, secondValue, difference, firstValue) &&
                difference.compareTo(maxDifference) <= 0;
    }

    private void addBetweenItems(Value_ item, Point_ itemIndex,
            Value_ firstBeforeItem, Value_ endOfBeforeSequenceItem, Point_ endOfBeforeSequenceItemIndex,
            Value_ firstAfterItem, Point_ startOfAfterSequenceIndex) {
        if (isFirstSuccessorOfSecond(itemIndex, item, endOfBeforeSequenceItemIndex, endOfBeforeSequenceItem)) {
            // We need to extend the first bag
            SequenceImpl<Value_, Difference_> prevBag = startItemToSequence.get(firstBeforeItem);
            if (isFirstSuccessorOfSecond(startOfAfterSequenceIndex, firstAfterItem, itemIndex, item)) {
                // We need to merge the two bags
                startItemToPreviousBreak.remove(firstAfterItem);
                SequenceImpl<Value_, Difference_> afterBag = startItemToSequence.remove(firstAfterItem);
                prevBag.merge(afterBag);
                Map.Entry<Value_, BreakImpl<Value_, Difference_>> maybeNextBreak =
                        startItemToPreviousBreak.higherEntry(firstAfterItem);
                if (maybeNextBreak != null) {
                    maybeNextBreak.getValue().setPreviousSequence(prevBag);
                }
            } else {
                prevBag.setEnd(item);
                BreakImpl<Value_, Difference_> nextBreak = startItemToPreviousBreak.get(firstAfterItem);
                nextBreak.setLength(differenceFunction.apply(itemIndex, startOfAfterSequenceIndex));
            }
        } else {
            // Don't need to extend the first bag
            if (isFirstSuccessorOfSecond(startOfAfterSequenceIndex, firstAfterItem, itemIndex, item)) {
                // We need to move the after bag to use item as key
                SequenceImpl<Value_, Difference_> afterBag = startItemToSequence.remove(firstAfterItem);
                afterBag.setStart(item);
                startItemToSequence.put(item, afterBag);
                BreakImpl<Value_, Difference_> prevBreak = startItemToPreviousBreak.remove(firstAfterItem);
                prevBreak.setLength(differenceFunction.apply(endOfBeforeSequenceItemIndex, itemIndex));
                startItemToPreviousBreak.put(item, prevBreak);
            } else {
                // Start a new bag of consecutive items
                SequenceImpl<Value_, Difference_> newBag = new SequenceImpl<>(this, item);
                startItemToSequence.put(item, newBag);
                BreakImpl<Value_, Difference_> nextBreak = startItemToPreviousBreak.get(firstAfterItem);
                nextBreak.setPreviousSequence(newBag);
                nextBreak.setLength(differenceFunction.apply(itemIndex, startOfAfterSequenceIndex));
                startItemToPreviousBreak.put(item, new BreakImpl<>(startItemToSequence.get(firstBeforeItem), newBag,
                        differenceFunction.apply(endOfBeforeSequenceItemIndex, itemIndex)));
            }
        }
    }

    private boolean removeItemFromBag(SequenceImpl<Value_, Difference_> bag, Value_ item, Value_ sequenceStart,
            Value_ sequenceEnd) {
        NavigableSet<Value_> itemSet = getItemSet();
        if (item.equals(sequenceStart)) {
            // Change start key to the item after this one
            bag.setStart(itemSet.higher(item));
            startItemToSequence.remove(sequenceStart);
            BreakImpl<Value_, Difference_> extendedBreak = startItemToPreviousBreak.remove(sequenceStart);
            Value_ firstItem = bag.getFirstItem();
            startItemToSequence.put(firstItem, bag);
            if (extendedBreak != null) {
                updateLengthOfBreak(extendedBreak);
                startItemToPreviousBreak.put(firstItem, extendedBreak);
            }
            indexMap.remove(item);
            return true;
        }
        if (item.equals(sequenceEnd)) {
            // Set end key to the item before this one
            bag.setEnd(itemSet.lower(item));
            Map.Entry<Value_, BreakImpl<Value_, Difference_>> extendedBreakEntry =
                    startItemToPreviousBreak.higherEntry(item);
            if (extendedBreakEntry != null) {
                BreakImpl<Value_, Difference_> extendedBreak = extendedBreakEntry.getValue();
                updateLengthOfBreak(extendedBreak);
            }
            indexMap.remove(item);
            return true;
        }

        Value_ firstAfterItem = bag.getItems().higher(item);
        Value_ firstBeforeItem = bag.getItems().lower(item);

        if (isFirstSuccessorOfSecond(
                indexMap.get(firstAfterItem), firstAfterItem,
                indexMap.get(firstBeforeItem), firstBeforeItem)) {
            // Bag is not split since the next two items are still close enough
            indexMap.remove(item);
            return true;
        }

        // Need to split bag into two halves
        // Both halves are not empty as the item was not an endpoint
        // Additional, the breaks before and after the broken sequence
        // are not affected since an endpoint was not removed
        SequenceImpl<Value_, Difference_> splitBag = bag.split(item);
        Value_ firstSplitItem = splitBag.getFirstItem();
        Value_ lastOriginalItem = bag.getLastItem();
        startItemToSequence.put(firstSplitItem, splitBag);
        startItemToPreviousBreak.put(firstSplitItem,
                new BreakImpl<>(bag, splitBag, getBreakLengthBetween(lastOriginalItem, firstSplitItem)));
        Map.Entry<Value_, BreakImpl<Value_, Difference_>> maybeNextBreak =
                startItemToPreviousBreak.higherEntry(firstAfterItem);
        if (maybeNextBreak != null) {
            maybeNextBreak.getValue().setPreviousSequence(splitBag);
        }
        indexMap.remove(item);
        return true;
    }

    @Override
    public String toString() {
        return "Sequences {" +
                "sequenceList=" + sequenceList +
                ", breakList=" + breakList +
                '}';
    }

    private static final class ValueComparator<Value_, Point_ extends Comparable<Point_>> implements Comparator<Value_> {

        private final Map<Value_, Point_> indexMap;

        public ValueComparator(Map<Value_, Point_> indexMap) {
            this.indexMap = Objects.requireNonNull(indexMap);
        }

        @Override
        public int compare(Value_ o1, Value_ o2) {
            if (o1 == o2) {
                return 0;
            }
            Point_ point1 = indexMap.get(o1);
            Point_ point2 = indexMap.get(o2);
            if (point1 == point2) {
                return compareWithIdentityHashCode(o1, o2);
            }
            int comparison = point1.compareTo(point2);
            if (comparison != 0) {
                return comparison;
            }
            return compareWithIdentityHashCode(o1, o2);
        }

        private static int compareWithIdentityHashCode(Object o1, Object o2) {
            // Identity Hashcode for duplicate protection; we must always include duplicates.
            // Ex: two different games on the same time slot
            int identityHashCode1 = System.identityHashCode(o1);
            int identityHashCode2 = System.identityHashCode(o2);
            return Integer.compare(identityHashCode1, identityHashCode2);
        }
    }

}
