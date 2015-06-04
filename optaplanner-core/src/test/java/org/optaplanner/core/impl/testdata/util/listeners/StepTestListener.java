package org.optaplanner.core.impl.testdata.util.listeners;

import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

import java.util.ArrayList;
import java.util.List;

public class StepTestListener extends AbstractStepTestListener{

    private List<AbstractStepScope> recordedSteps = new ArrayList<AbstractStepScope>();

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        recordedSteps.add(stepScope);
    }

    public List<AbstractStepScope> getRecordedSteps() {
        return recordedSteps;
    }

}
