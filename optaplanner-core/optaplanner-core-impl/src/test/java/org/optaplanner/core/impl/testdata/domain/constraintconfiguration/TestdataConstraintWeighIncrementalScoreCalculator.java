package org.optaplanner.core.impl.testdata.domain.constraintconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

public final class TestdataConstraintWeighIncrementalScoreCalculator
        implements IncrementalScoreCalculator<TestdataConstraintConfigurationSolution, SimpleScore> {

    private TestdataConstraintConfigurationSolution workingSolution;
    private List<TestdataEntity> entityList;

    @Override
    public void resetWorkingSolution(TestdataConstraintConfigurationSolution workingSolution) {
        this.workingSolution = workingSolution;
        this.entityList = new ArrayList<>(workingSolution.getEntityList());
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // No need to do anything.
    }

    @Override
    public void afterEntityAdded(Object entity) {
        entityList.add((TestdataEntity) entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        throw new UnsupportedOperationException(); // Will not be called.
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        throw new UnsupportedOperationException(); // Will not be called.
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        // No need to do anything.
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        entityList.remove((TestdataEntity) entity);
    }

    @Override
    public SimpleScore calculateScore() {
        SimpleScore constraintWeight = workingSolution.getConstraintConfiguration().getFirstWeight();
        return constraintWeight.multiply(entityList.size());
    }
}
