package org.optaplanner.core.impl.testdata.util.listeners;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

import java.util.ArrayList;
import java.util.List;

public class PhaseTestListener extends AbstractPhaseTestListener {

    private List<AbstractPhaseScope> phaseScopeList = new ArrayList<AbstractPhaseScope>();

    public List<AbstractPhaseScope> getPhaseScopeList() {
        return phaseScopeList;
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {

    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {

    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {

    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        phaseScopeList.add(phaseScope);
    }

}
