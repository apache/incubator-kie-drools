package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;

public abstract class AbstractRandomSwapIterator<Solution_, Move_ extends Move<Solution_>, SubSelection_>
        extends UpcomingSelectionIterator<Move_> {

    protected final Iterable<SubSelection_> leftSubSelector;
    protected final Iterable<SubSelection_> rightSubSelector;

    protected Iterator<SubSelection_> leftSubSelectionIterator;
    protected Iterator<SubSelection_> rightSubSelectionIterator;

    public AbstractRandomSwapIterator(Iterable<SubSelection_> leftSubSelector,
            Iterable<SubSelection_> rightSubSelector) {
        this.leftSubSelector = leftSubSelector;
        this.rightSubSelector = rightSubSelector;
        leftSubSelectionIterator = this.leftSubSelector.iterator();
        rightSubSelectionIterator = this.rightSubSelector.iterator();
        // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
    }

    @Override
    protected Move_ createUpcomingSelection() {
        // Ideally, this code should have read:
        //     SubS leftSubSelection = leftSubSelectionIterator.next();
        //     SubS rightSubSelection = rightSubSelectionIterator.next();
        // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
        if (!leftSubSelectionIterator.hasNext()) {
            leftSubSelectionIterator = leftSubSelector.iterator();
            if (!leftSubSelectionIterator.hasNext()) {
                return noUpcomingSelection();
            }
        }
        SubSelection_ leftSubSelection = leftSubSelectionIterator.next();
        if (!rightSubSelectionIterator.hasNext()) {
            rightSubSelectionIterator = rightSubSelector.iterator();
            if (!rightSubSelectionIterator.hasNext()) {
                return noUpcomingSelection();
            }
        }
        SubSelection_ rightSubSelection = rightSubSelectionIterator.next();
        return newSwapSelection(leftSubSelection, rightSubSelection);
    }

    protected abstract Move_ newSwapSelection(SubSelection_ leftSubSelection, SubSelection_ rightSubSelection);

}
