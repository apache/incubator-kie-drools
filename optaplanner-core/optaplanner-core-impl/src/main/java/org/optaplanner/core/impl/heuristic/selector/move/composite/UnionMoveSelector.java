package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * A {@link CompositeMoveSelector} that unions 2 or more {@link MoveSelector}s.
 * <p>
 * For example: a union of {A, B, C} and {X, Y} will result in {A, B, C, X, Y}.
 * <p>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B, C} and {B, D} will result in {A, B, C, B, D}.
 *
 * @see CompositeMoveSelector
 */
public class UnionMoveSelector<Solution_> extends CompositeMoveSelector<Solution_> {

    protected final SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory;

    protected ScoreDirector<Solution_> scoreDirector;

    public UnionMoveSelector(List<MoveSelector<Solution_>> childMoveSelectorList, boolean randomSelection) {
        this(childMoveSelectorList, randomSelection, null);
    }

    public UnionMoveSelector(List<MoveSelector<Solution_>> childMoveSelectorList, boolean randomSelection,
            SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory) {
        super(childMoveSelectorList, randomSelection);
        this.selectorProbabilityWeightFactory = selectorProbabilityWeightFactory;
        if (!randomSelection) {
            if (selectorProbabilityWeightFactory != null) {
                throw new IllegalArgumentException("The selector (" + this
                        + ") without randomSelection (" + randomSelection
                        + ") cannot have a selectorProbabilityWeightFactory (" + selectorProbabilityWeightFactory
                        + ").");
            }
        }
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        scoreDirector = stepScope.getScoreDirector();
        super.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        scoreDirector = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        if (randomSelection) {
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                if (moveSelector.isNeverEnding()) {
                    return true;
                }
            }
            // The UnionMoveSelector is special: it can be randomSelection true and still neverEnding false
            return false;
        } else {
            // Only the last childMoveSelector can be neverEnding
            return !childMoveSelectorList.isEmpty()
                    && childMoveSelectorList.get(childMoveSelectorList.size() - 1).isNeverEnding();
        }
    }

    @Override
    public long getSize() {
        long size = 0L;
        for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
            size += moveSelector.getSize();
        }
        return size;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            Stream<Move<Solution_>> stream = Stream.empty();
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                stream = Stream.concat(stream, toStream(moveSelector));
            }
            return stream.iterator();
        } else if (selectorProbabilityWeightFactory == null) {
            return new UniformRandomUnionMoveIterator<>(childMoveSelectorList, workingRandom);
        } else {
            return new BiasedRandomUnionMoveIterator<>(childMoveSelectorList,
                    moveSelector -> {
                        double weight = selectorProbabilityWeightFactory.createProbabilityWeight(scoreDirector, moveSelector);
                        if (weight < 0.0) {
                            throw new IllegalStateException(
                                    "The selectorProbabilityWeightFactory (" + selectorProbabilityWeightFactory
                                            + ") returned a negative probabilityWeight (" + weight + ").");
                        }
                        return weight;
                    }, workingRandom);
        }
    }

    private static <Solution_> Stream<Move<Solution_>> toStream(MoveSelector<Solution_> moveSelector) {
        return StreamSupport.stream(moveSelector.spliterator(), false);
    }

    @Override
    public String toString() {
        return "Union(" + childMoveSelectorList + ")";
    }

}
