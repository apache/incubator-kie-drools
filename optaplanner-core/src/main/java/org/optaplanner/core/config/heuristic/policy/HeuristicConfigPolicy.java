package org.optaplanner.core.config.heuristic.policy;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.EntityMimicRecorder;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class HeuristicConfigPolicy {

    private final EnvironmentMode environmentMode;
    private final SolutionDescriptor solutionDescriptor;
    private final ScoreDefinition scoreDefinition;

    private boolean sortEntitiesByDecreasingDifficultyEnabled = false;
    private boolean sortValuesByIncreasingStrengthEnabled = false;
    private boolean reinitializeVariableFilterEnabled = false;
    private boolean initializedChainedValueFilterEnabled = false;

    private Map<String, EntityMimicRecorder> entityMimicRecorderMap
            = new HashMap<String, EntityMimicRecorder>();

    public HeuristicConfigPolicy(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition) {
        this.environmentMode = environmentMode;
        this.solutionDescriptor = solutionDescriptor;
        this.scoreDefinition = scoreDefinition;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public boolean isSortEntitiesByDecreasingDifficultyEnabled() {
        return sortEntitiesByDecreasingDifficultyEnabled;
    }

    public void setSortEntitiesByDecreasingDifficultyEnabled(boolean sortEntitiesByDecreasingDifficultyEnabled) {
        this.sortEntitiesByDecreasingDifficultyEnabled = sortEntitiesByDecreasingDifficultyEnabled;
    }

    public boolean isSortValuesByIncreasingStrengthEnabled() {
        return sortValuesByIncreasingStrengthEnabled;
    }

    public void setSortValuesByIncreasingStrengthEnabled(boolean sortValuesByIncreasingStrengthEnabled) {
        this.sortValuesByIncreasingStrengthEnabled = sortValuesByIncreasingStrengthEnabled;
    }

    public boolean isReinitializeVariableFilterEnabled() {
        return reinitializeVariableFilterEnabled;
    }

    public void setReinitializeVariableFilterEnabled(boolean reinitializeVariableFilterEnabled) {
        this.reinitializeVariableFilterEnabled = reinitializeVariableFilterEnabled;
    }

    public Map<String, EntityMimicRecorder> getEntityMimicRecorderMap() {
        return entityMimicRecorderMap;
    }

    public void setEntityMimicRecorderMap(Map<String, EntityMimicRecorder> entityMimicRecorderMap) {
        this.entityMimicRecorderMap = entityMimicRecorderMap;
    }

    public boolean isInitializedChainedValueFilterEnabled() {
        return initializedChainedValueFilterEnabled;
    }

    public void setInitializedChainedValueFilterEnabled(boolean initializedChainedValueFilterEnabled) {
        this.initializedChainedValueFilterEnabled = initializedChainedValueFilterEnabled;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public HeuristicConfigPolicy createPhaseConfigPolicy() {
        return new HeuristicConfigPolicy(environmentMode, solutionDescriptor, scoreDefinition);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addEntityMimicRecorder(String id, EntityMimicRecorder mimicRecordingEntitySelector) {
        EntityMimicRecorder put = entityMimicRecorderMap.put(id, mimicRecordingEntitySelector);
        if (put != null) {
            throw new IllegalStateException("Multiple " + EntityMimicRecorder.class.getSimpleName() + "s (usually "
                    + EntitySelector.class.getSimpleName() + "s) have the same id (" + id + ").");
        }
    }

    public EntityMimicRecorder getEntityMimicRecorder(String id) {
        return entityMimicRecorderMap.get(id);
    }

}
