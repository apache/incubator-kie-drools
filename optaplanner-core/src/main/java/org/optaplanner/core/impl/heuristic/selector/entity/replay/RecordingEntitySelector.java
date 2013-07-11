package org.optaplanner.core.impl.heuristic.selector.entity.replay;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;

public class RecordingEntitySelector extends AbstractEntitySelector {

    protected final EntitySelector childEntitySelector;

    protected boolean hasRecordedSelectionCreated;
    protected boolean hasRecordedSelection;
    protected boolean recordedSelectionCreated;
    protected Object recordedSelection;

    public RecordingEntitySelector(EntitySelector childEntitySelector) {
        this.childEntitySelector = childEntitySelector;
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    public boolean hasRecordedSelection() {
        if (!hasRecordedSelectionCreated) {
            throw new IllegalStateException("The method hasRecordedSelection() fails on the selector (" + this
                    + ") because it hasNext() hasn't been called yet.");
        }
        return hasRecordedSelection;
    }

    public Object getRecordedSelection() {
        if (!recordedSelectionCreated) {
            throw new IllegalStateException("The method getRecordedSelection() fails on the selector (" + this
                    + ") because it next() hasn't been called yet.");
        }
        return recordedSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        hasRecordedSelectionCreated = false;
        recordedSelectionCreated = false;
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        hasRecordedSelectionCreated = false;
        hasRecordedSelection = false;
        recordedSelectionCreated = false;
        recordedSelection = null;
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return childEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return childEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return childEntitySelector.getSize();
    }

    public Iterator<Object> iterator() {
        return new RecordingEntityIterator(childEntitySelector.iterator());
    }

    private class RecordingEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;

        public RecordingEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
        }

        public boolean hasNext() {
            hasRecordedSelection = childEntityIterator.hasNext();
            hasRecordedSelectionCreated = true;
            return hasRecordedSelection;
        }

        public Object next() {
            recordedSelection = childEntityIterator.next();
            recordedSelectionCreated = true;
            return recordedSelection;
        }

    }

    public Iterator<Object> endingIterator() {
        // No recording, because the endingIterator() is used for determining size
        return childEntitySelector.endingIterator();
    }

    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Recording(" + childEntitySelector + ")";
    }

}
