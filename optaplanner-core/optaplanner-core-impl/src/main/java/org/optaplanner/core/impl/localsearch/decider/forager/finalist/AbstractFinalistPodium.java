package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Abstract superclass for {@link FinalistPodium}.
 *
 * @see FinalistPodium
 */
public abstract class AbstractFinalistPodium<Solution_> extends LocalSearchPhaseLifecycleListenerAdapter<Solution_>
        implements FinalistPodium<Solution_> {

    protected static final int FINALIST_LIST_MAX_SIZE = 1_024_000;

    protected boolean finalistIsAccepted;
    protected List<LocalSearchMoveScope<Solution_>> finalistList = new ArrayList<>(1024);

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        finalistIsAccepted = false;
        finalistList.clear();
    }

    protected void clearAndAddFinalist(LocalSearchMoveScope<Solution_> moveScope) {
        finalistList.clear();
        finalistList.add(moveScope);
    }

    protected void addFinalist(LocalSearchMoveScope<Solution_> moveScope) {
        if (finalistList.size() >= FINALIST_LIST_MAX_SIZE) {
            // Avoid unbounded growth and OutOfMemoryException
            return;
        }
        finalistList.add(moveScope);
    }

    @Override
    public List<LocalSearchMoveScope<Solution_>> getFinalistList() {
        return finalistList;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        finalistIsAccepted = false;
        finalistList.clear();
    }

}
