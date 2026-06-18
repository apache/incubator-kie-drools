/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.bavet.common;

import static org.optaplanner.constraint.streams.bavet.common.BavetTupleState.DEAD;

import java.util.ArrayDeque;
import java.util.Queue;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleList;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

/**
 * This class has two direct children: {@link AbstractIndexedIfExistsNode} and {@link AbstractUnindexedIfExistsNode}.
 * The logic in either is identical, except that the latter removes all indexing work.
 * Therefore any time that one of the classes changes,
 * the other should be inspected if it could benefit from applying the change there too.
 *
 * @param <LeftTuple_>
 * @param <Right_>
 */
public abstract class AbstractIfExistsNode<LeftTuple_ extends Tuple, Right_>
        extends AbstractNode
        implements LeftTupleLifecycle<LeftTuple_>, RightTupleLifecycle<UniTuple<Right_>> {

    protected final boolean shouldExist;

    protected final int inputStoreIndexLeftTrackerList; // -1 if !isFiltering
    protected final int inputStoreIndexRightTrackerList; // -1 if !isFiltering

    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)}, and/or ...
     */
    private final TupleLifecycle<LeftTuple_> nextNodesTupleLifecycle;
    protected final boolean isFiltering;
    // No outputStoreSize because this node is not a tuple source, even though it has a dirtyCounterQueue.
    protected final Queue<ExistsCounter<LeftTuple_>> dirtyCounterQueue;

    protected AbstractIfExistsNode(boolean shouldExist,
            int inputStoreIndexLeftTrackerList, int inputStoreIndexRightTrackerList,
            TupleLifecycle<LeftTuple_> nextNodesTupleLifecycle,
            boolean isFiltering) {
        this.shouldExist = shouldExist;
        this.inputStoreIndexLeftTrackerList = inputStoreIndexLeftTrackerList;
        this.inputStoreIndexRightTrackerList = inputStoreIndexRightTrackerList;
        this.nextNodesTupleLifecycle = nextNodesTupleLifecycle;
        this.isFiltering = isFiltering;
        this.dirtyCounterQueue = new ArrayDeque<>(1000);
    }

    protected abstract boolean testFiltering(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    protected void initCounterLeft(ExistsCounter<LeftTuple_> counter) {
        if (shouldExist ? counter.countRight > 0 : counter.countRight == 0) {
            // Counters start out dead
            counter.state = BavetTupleState.CREATING;
            dirtyCounterQueue.add(counter);
        }
    }

    protected final void updateUnchangedCounterLeft(ExistsCounter<LeftTuple_> counter) {
        switch (counter.state) {
            case CREATING:
            case UPDATING:
            case DYING:
            case ABORTING:
            case DEAD:
                // Counter state does not change because the index properties didn't change
                break;
            case OK:
                // Still needed to propagate the update for downstream filters, matchWeighers, ...
                counter.state = BavetTupleState.UPDATING;
                dirtyCounterQueue.add(counter);
                break;
            default:
                throw new IllegalStateException("Impossible state: The counter (" + counter.state + ") in node (" +
                        this + ") is in an unexpected state (" + counter.state + ").");
        }
    }

    protected void updateCounterLeft(ExistsCounter<LeftTuple_> counter) {
        if (shouldExist ? counter.countRight > 0 : counter.countRight == 0) {
            // Insert or update
            switch (counter.state) {
                case CREATING:
                case UPDATING:
                    // Don't add the tuple to the dirtyTupleQueue twice
                    break;
                case OK:
                    counter.state = BavetTupleState.UPDATING;
                    dirtyCounterQueue.add(counter);
                    break;
                case DYING:
                    counter.state = BavetTupleState.UPDATING;
                    break;
                case DEAD:
                    counter.state = BavetTupleState.CREATING;
                    dirtyCounterQueue.add(counter);
                    break;
                case ABORTING:
                    counter.state = BavetTupleState.CREATING;
                    break;
                default:
                    throw new IllegalStateException("Impossible state: the counter (" + counter
                            + ") has an impossible insert state (" + counter.state + ").");
            }
        } else {
            // Retract or remain dead
            switch (counter.state) {
                case CREATING:
                    // Kill it before it propagates
                    counter.state = BavetTupleState.ABORTING;
                    break;
                case UPDATING:
                    // Kill the original propagation
                    counter.state = BavetTupleState.DYING;
                    break;
                case OK:
                    counter.state = BavetTupleState.DYING;
                    dirtyCounterQueue.add(counter);
                    break;
                case DYING:
                case DEAD:
                case ABORTING:
                    // Don't add the tuple to the dirtyTupleQueue twice
                    break;
                default:
                    throw new IllegalStateException("Impossible state: The counter (" + counter
                            + ") has an impossible retract state (" + counter.state + ").");
            }
        }
    }

    protected void killCounterLeft(ExistsCounter<LeftTuple_> counter) {
        if (shouldExist ? counter.countRight > 0 : counter.countRight == 0) {
            doRetractCounter(counter);
        }
    }

    protected void incrementCounterRight(ExistsCounter<LeftTuple_> counter) {
        if (counter.countRight == 0) {
            if (shouldExist) {
                doInsertCounter(counter);
            } else {
                doRetractCounter(counter);
            }
        } // Else do not even propagate an update
        counter.countRight++;
    }

    protected void decrementCounterRight(ExistsCounter<LeftTuple_> counter) {
        counter.countRight--;
        if (counter.countRight == 0) {
            if (shouldExist) {
                doRetractCounter(counter);
            } else {
                doInsertCounter(counter);
            }
        } // Else do not even propagate an update
    }

    protected TupleList<FilteringTracker<LeftTuple_>> updateRightTrackerList(UniTuple<Right_> rightTuple) {
        TupleList<FilteringTracker<LeftTuple_>> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
        rightTrackerList.forEach(filteringTacker -> {
            decrementCounterRight(filteringTacker.counter);
            filteringTacker.remove();
        });
        return rightTrackerList;
    }

    protected void updateCounterFromLeft(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple, ExistsCounter<LeftTuple_> counter,
            TupleList<FilteringTracker<LeftTuple_>> leftTrackerList) {
        if (testFiltering(leftTuple, rightTuple)) {
            counter.countRight++;
            TupleList<FilteringTracker<LeftTuple_>> rightTrackerList = rightTuple.getStore(inputStoreIndexRightTrackerList);
            new FilteringTracker<>(counter, leftTrackerList, rightTrackerList);
        }
    }

    protected void updateCounterFromRight(UniTuple<Right_> rightTuple, ExistsCounter<LeftTuple_> counter,
            TupleList<FilteringTracker<LeftTuple_>> rightTrackerList) {
        if (testFiltering(counter.leftTuple, rightTuple)) {
            incrementCounterRight(counter);
            TupleList<FilteringTracker<LeftTuple_>> leftTrackerList =
                    counter.leftTuple.getStore(inputStoreIndexLeftTrackerList);
            new FilteringTracker<>(counter, leftTrackerList, rightTrackerList);
        }
    }

    private void doInsertCounter(ExistsCounter<LeftTuple_> counter) {
        switch (counter.state) {
            case DYING:
                counter.state = BavetTupleState.UPDATING;
                break;
            case DEAD:
                counter.state = BavetTupleState.CREATING;
                dirtyCounterQueue.add(counter);
                break;
            case ABORTING:
                counter.state = BavetTupleState.CREATING;
                break;
            default:
                throw new IllegalStateException("Impossible state: the counter (" + counter
                        + ") has an impossible insert state (" + counter.state + ").");
        }
    }

    private void doRetractCounter(ExistsCounter<LeftTuple_> counter) {
        switch (counter.state) {
            case CREATING:
                // Kill it before it propagates
                counter.state = BavetTupleState.ABORTING;
                break;
            case UPDATING:
                // Kill the original propagation
                counter.state = BavetTupleState.DYING;
                break;
            case OK:
                counter.state = BavetTupleState.DYING;
                dirtyCounterQueue.add(counter);
                break;
            default:
                throw new IllegalStateException("Impossible state: The counter (" + counter
                        + ") has an impossible retract state (" + counter.state + ").");
        }
    }

    @Override
    public final void calculateScore() {
        for (ExistsCounter<LeftTuple_> counter : dirtyCounterQueue) {
            switch (counter.state) {
                case CREATING:
                    nextNodesTupleLifecycle.insert(counter.leftTuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case UPDATING:
                    nextNodesTupleLifecycle.update(counter.leftTuple);
                    counter.state = BavetTupleState.OK;
                    break;
                case DYING:
                    nextNodesTupleLifecycle.retract(counter.leftTuple);
                    counter.state = DEAD;
                    break;
                case ABORTING:
                    counter.state = DEAD;
                    break;
                case OK:
                case DEAD:
                default:
                    throw new IllegalStateException("Impossible state: The dirty counter (" + counter
                            + ") has an non-dirty state (" + counter.state + ").");
            }
        }
        dirtyCounterQueue.clear();
    }

    protected static final class FilteringTracker<LeftTuple_ extends Tuple> {
        final ExistsCounter<LeftTuple_> counter;
        private final TupleListEntry<FilteringTracker<LeftTuple_>> leftTrackerEntry;
        private final TupleListEntry<FilteringTracker<LeftTuple_>> rightTrackerEntry;

        FilteringTracker(ExistsCounter<LeftTuple_> counter, TupleList<FilteringTracker<LeftTuple_>> leftTrackerList,
                TupleList<FilteringTracker<LeftTuple_>> rightTrackerList) {
            this.counter = counter;
            leftTrackerEntry = leftTrackerList.add(this);
            rightTrackerEntry = rightTrackerList.add(this);
        }

        public void remove() {
            leftTrackerEntry.remove();
            rightTrackerEntry.remove();
        }

    }

}
