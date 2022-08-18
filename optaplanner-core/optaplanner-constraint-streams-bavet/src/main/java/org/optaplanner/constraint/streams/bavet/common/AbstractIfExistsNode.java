package org.optaplanner.constraint.streams.bavet.common;

import static org.optaplanner.constraint.streams.bavet.common.BavetTupleState.DEAD;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

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
    /**
     * Calls for example {@link AbstractScorer#insert(Tuple)}, and/or ...
     */
    private final TupleLifecycle<LeftTuple_> nextNodesTupleLifecycle;

    // No outputStoreSize because this node is not a tuple source, even though it has a dirtyCounterQueue.
    protected final Queue<ExistsCounter<LeftTuple_>> dirtyCounterQueue;
    protected final boolean isFiltering;

    protected AbstractIfExistsNode(boolean shouldExist, TupleLifecycle<LeftTuple_> nextNodeTupleLifecycle,
            boolean isFiltering) {
        this.shouldExist = shouldExist;
        this.nextNodesTupleLifecycle = nextNodeTupleLifecycle;
        this.dirtyCounterQueue = new ArrayDeque<>(1000);
        this.isFiltering = isFiltering;
    }

    protected abstract boolean testFiltering(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple);

    protected final void processInsert(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple, ExistsCounter<LeftTuple_> counter,
            Set<ExistsCounter<LeftTuple_>> counterSetRight) {
        if (!isFiltering || testFiltering(leftTuple, rightTuple)) {
            if (counter.countRight++ == 0) {
                if (shouldExist) {
                    insertCounter(counter);
                } else {
                    retractCounter(counter);
                }
            }
            counterSetRight.add(counter);
        }
    }

    protected final void processUpdate(LeftTuple_ leftTuple, UniTuple<Right_> rightTuple, ExistsCounter<LeftTuple_> counter,
            Set<ExistsCounter<LeftTuple_>> counterSetRight) {
        if (testFiltering(leftTuple, rightTuple)) {
            if (counter.countRight++ == 0) {
                if (shouldExist) {
                    insertOrUpdateCounter(counter);
                } else {
                    retractOrRemainDeadCounter(counter);
                }
            }
            counterSetRight.add(counter);
        }
    }

    protected final void processAndClearCounters(Set<ExistsCounter<LeftTuple_>> counterSetRight) {
        processCounters(counterSetRight);
        counterSetRight.clear();
    }

    protected final void processCounters(Set<ExistsCounter<LeftTuple_>> counterSetRight) {
        for (ExistsCounter<LeftTuple_> counter : counterSetRight) {
            if (--counter.countRight == 0) {
                if (shouldExist) {
                    retractCounter(counter);
                } else {
                    insertCounter(counter);
                }
            }
        }
    }

    protected final void processCounterUpdate(ExistsCounter<LeftTuple_> counter) {
        switch (counter.state) {
            case CREATING:
            case UPDATING:
            case DYING:
            case ABORTING:
            case DEAD:
                // Counter state does not change because the index properties didn't change
                break;
            case OK:
                // Still needed to propagate the update for downstream filters, matchWeighters, ...
                counter.state = BavetTupleState.UPDATING;
                dirtyCounterQueue.add(counter);
                break;
            default:
                throw new IllegalStateException("Impossible state: The counter (" + counter.state + ") in node (" +
                        this + ") is in an unexpected state (" + counter.state + ").");
        }
    }

    private void insertCounter(ExistsCounter<LeftTuple_> counter) {
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

    protected final void insertOrUpdateCounter(ExistsCounter<LeftTuple_> counter) {
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
    }

    protected final void retractOrRemainDeadCounter(ExistsCounter<LeftTuple_> counter) {
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

    protected final void retractCounter(ExistsCounter<LeftTuple_> counter) {
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

}
