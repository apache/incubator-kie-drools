/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.experimental.impl;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.Sequence;

/**
 * A ConsecutiveSetTree determine what value are consecutive. A sequence x1,x2,x3,...,xn
 * is understood to be consecutive by d iff x2 - x1 <= d, x3 -x2 <= d, ..., xn - x(n-1) <= d.
 * This datastructure can be thought as an interval tree that maps the point p to
 * the interval [p, p + d].
 *
 * @param <Value_> The type of value stored (examples: shifts)
 * @param <Point_> The type of the point (examples: int, LocalDateTime)
 * @param <Difference_> The type of the difference (examples: int, Duration)
 */
public class ConsecutiveSetTree<Value_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>> {
    private final Function<Value_, Point_> indexFunction;
    private final BiFunction<Point_, Point_, Difference_> differenceFunction;
    private final BiFunction<Difference_, Difference_, Difference_> sumFunction;
    private final Difference_ maxDifference;
    private final Difference_ zeroDifference;
    private final Map<Value_, Integer> itemToCountMap;
    private final NavigableSet<Value_> itemSet;
    private final NavigableMap<Value_, SequenceImpl<Value_, Difference_>> startItemToSequence;
    private final NavigableMap<Value_, BreakImpl<Value_, Difference_>> startItemToPreviousBreak;

    private final MapValuesIterable<Value_, SequenceImpl<Value_, Difference_>> sequenceList;
    private final MapValuesIterable<Value_, BreakImpl<Value_, Difference_>> breakList;

    private final ConsecutiveDataImpl<Value_, Difference_> consecutiveData;

    public ConsecutiveSetTree(Function<Value_, Point_> indexFunction,
            BiFunction<Point_, Point_, Difference_> differenceFunction,
            BiFunction<Difference_, Difference_, Difference_> sumFunction,
            Difference_ maxDifference,
            Difference_ zeroDifference) {
        this.indexFunction = indexFunction;
        this.differenceFunction = differenceFunction;
        this.sumFunction = sumFunction;
        this.maxDifference = maxDifference;
        this.zeroDifference = zeroDifference;
        // Identity Hashcode for duplicate protection
        // Ex: two different games on the same time slot
        Comparator<Value_> comparator = Comparator.comparing(indexFunction).thenComparing(System::identityHashCode);
        itemToCountMap = new IdentityHashMap<>();
        itemSet = new TreeSet<>(comparator);
        startItemToSequence = new TreeMap<>(comparator);
        startItemToPreviousBreak = new TreeMap<>(comparator);
        consecutiveData = new ConsecutiveDataImpl<>(this);
        sequenceList = new MapValuesIterable<>(startItemToSequence);
        breakList = new MapValuesIterable<>(startItemToPreviousBreak);
    }

    // Public API
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Iterable<Sequence<Value_, Difference_>> getConsecutiveSequences() {
        return (Iterable) sequenceList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Iterable<Break<Value_, Difference_>> getBreaks() {
        return (Iterable) breakList;
    }

    public ConsecutiveInfo<Value_, Difference_> getConsecutiveData() {
        return consecutiveData;
    }

    public boolean add(Value_ item) {
        int newCount = itemToCountMap.compute(item, (key, count) -> count == null ? 1 : count + 1);
        if (newCount > 1) { // Item already in bag.
            return true;
        }
        itemSet.add(item);
        Value_ firstBeforeItem = startItemToSequence.floorKey(item);
        Point_ itemIndex = indexFunction.apply(item);
        if (firstBeforeItem != null) {
            Value_ endOfBeforeSequenceItem = getEndItem(firstBeforeItem);
            Point_ endOfBeforeSequenceIndex = indexFunction.apply(endOfBeforeSequenceItem);
            if (isInNaturalOrderAndHashOrderIfEqual(itemIndex, item, endOfBeforeSequenceIndex, endOfBeforeSequenceItem)) {
                // Item is already in the bag; do nothing
                return true;
            } else {
                // Item is outside the bag
                Value_ firstAfterItem = startItemToSequence.higherKey(item);
                if (firstAfterItem != null) {
                    Point_ startOfAfterSequenceIndex = indexFunction.apply(firstAfterItem);
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
                Point_ startOfAfterSequenceIndex = indexFunction.apply(firstAfterItem);

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
        itemSet.remove(item);

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
            return true;
        }

        // Bag is not empty
        return removeItemFromBag(bag, item, firstBeforeItem, endItem);
    }

    // Protected API

    protected NavigableSet<Value_> getItemSet() {
        return itemSet;
    }

    protected void updateLengthOfBreak(BreakImpl<Value_, Difference_> theBreak) {
        theBreak.setLength(getBreakLengthBetween(theBreak.getPreviousSequenceEnd(), theBreak.getNextSequenceStart()));
    }

    protected Difference_ getSequenceLength(Sequence<Value_, Difference_> sequence) {
        return sumFunction.apply(maxDifference, differenceFunction.apply(indexFunction.apply(sequence.getFirstItem()),
                indexFunction.apply(sequence.getLastItem())));
    }

    protected Difference_ getBreakLengthBetween(Value_ from, Value_ to) {
        return differenceFunction.apply(indexFunction.apply(from), indexFunction.apply(to));
    }

    protected Value_ getEndItem(Value_ key) {
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
            return true;
        }

        Value_ firstAfterItem = bag.getItems().higher(item);
        Value_ firstBeforeItem = bag.getItems().lower(item);

        if (isFirstSuccessorOfSecond(
                indexFunction.apply(firstAfterItem), firstAfterItem,
                indexFunction.apply(firstBeforeItem), firstBeforeItem)) {
            // Bag is not split since the next two items are still close enough
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
        return true;
    }

}
