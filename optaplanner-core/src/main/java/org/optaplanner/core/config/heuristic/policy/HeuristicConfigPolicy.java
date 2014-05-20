package org.optaplanner.core.config.heuristic.policy;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.EntityMimicRecorder;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;

public class HeuristicConfigPolicy {

    private final EnvironmentMode environmentMode;
    private final InnerScoreDirectorFactory scoreDirectorFactory;

    private EntitySorterManner entitySorterManner = EntitySorterManner.NONE;
    private ValueSorterManner valueSorterManner = ValueSorterManner.NONE;
    private boolean reinitializeVariableFilterEnabled = false;
    private boolean initializedChainedValueFilterEnabled = false;

    private Map<String, EntityMimicRecorder> entityMimicRecorderMap
            = new HashMap<String, EntityMimicRecorder>();

    public HeuristicConfigPolicy(EnvironmentMode environmentMode, InnerScoreDirectorFactory scoreDirectorFactory) {
        this.environmentMode = environmentMode;
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return scoreDirectorFactory.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirectorFactory.getScoreDefinition();
    }

    public InnerScoreDirectorFactory getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    public EntitySorterManner getEntitySorterManner() {
        return entitySorterManner;
    }

    public void setEntitySorterManner(EntitySorterManner entitySorterManner) {
        this.entitySorterManner = entitySorterManner;
    }

    public ValueSorterManner getValueSorterManner() {
        return valueSorterManner;
    }

    public void setValueSorterManner(ValueSorterManner valueSorterManner) {
        this.valueSorterManner = valueSorterManner;
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
        return new HeuristicConfigPolicy(environmentMode, scoreDirectorFactory);
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
