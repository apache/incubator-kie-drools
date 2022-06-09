package org.optaplanner.core.impl.heuristic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.EntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.ValueMimicRecorder;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.thread.DefaultSolverThreadFactory;

public class HeuristicConfigPolicy<Solution_> {

    private final EnvironmentMode environmentMode;
    private final String logIndentation;
    private final Integer moveThreadCount;
    private final Integer moveThreadBufferSize;
    private final Class<? extends ThreadFactory> threadFactoryClass;
    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;

    private final EntitySorterManner entitySorterManner;
    private final ValueSorterManner valueSorterManner;

    private final boolean reinitializeVariableFilterEnabled;
    private final boolean initializedChainedValueFilterEnabled;

    private final Map<String, EntityMimicRecorder<Solution_>> entityMimicRecorderMap = new HashMap<>();
    private final Map<String, ValueMimicRecorder<Solution_>> valueMimicRecorderMap = new HashMap<>();

    private HeuristicConfigPolicy(Builder<Solution_> builder) {
        this.environmentMode = builder.environmentMode;
        this.logIndentation = builder.logIndentation;
        this.moveThreadCount = builder.moveThreadCount;
        this.moveThreadBufferSize = builder.moveThreadBufferSize;
        this.threadFactoryClass = builder.threadFactoryClass;
        this.scoreDirectorFactory = builder.scoreDirectorFactory;
        this.entitySorterManner = builder.entitySorterManner;
        this.valueSorterManner = builder.valueSorterManner;
        this.reinitializeVariableFilterEnabled = builder.reinitializeVariableFilterEnabled;
        this.initializedChainedValueFilterEnabled = builder.initializedChainedValueFilterEnabled;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public String getLogIndentation() {
        return logIndentation;
    }

    public Integer getMoveThreadCount() {
        return moveThreadCount;
    }

    public Integer getMoveThreadBufferSize() {
        return moveThreadBufferSize;
    }

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return scoreDirectorFactory.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirectorFactory.getScoreDefinition();
    }

    public InnerScoreDirectorFactory<Solution_, ?> getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    public EntitySorterManner getEntitySorterManner() {
        return entitySorterManner;
    }

    public ValueSorterManner getValueSorterManner() {
        return valueSorterManner;
    }

    public boolean isReinitializeVariableFilterEnabled() {
        return reinitializeVariableFilterEnabled;
    }

    public boolean isInitializedChainedValueFilterEnabled() {
        return initializedChainedValueFilterEnabled;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Builder<Solution_> cloneBuilder() {
        return new Builder<>(environmentMode, moveThreadCount, moveThreadBufferSize, threadFactoryClass, scoreDirectorFactory)
                .withLogIndentation(logIndentation);
    }

    public HeuristicConfigPolicy<Solution_> createPhaseConfigPolicy() {
        return cloneBuilder().build();
    }

    public HeuristicConfigPolicy<Solution_> createChildThreadConfigPolicy(ChildThreadType childThreadType) {
        return cloneBuilder()
                .withLogIndentation(logIndentation + "        ")
                .build();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addEntityMimicRecorder(String id, EntityMimicRecorder<Solution_> mimicRecordingEntitySelector) {
        EntityMimicRecorder<Solution_> put = entityMimicRecorderMap.put(id, mimicRecordingEntitySelector);
        if (put != null) {
            throw new IllegalStateException("Multiple " + EntityMimicRecorder.class.getSimpleName() + "s (usually "
                    + EntitySelector.class.getSimpleName() + "s) have the same id (" + id + ").");
        }
    }

    public EntityMimicRecorder<Solution_> getEntityMimicRecorder(String id) {
        return entityMimicRecorderMap.get(id);
    }

    public void addValueMimicRecorder(String id, ValueMimicRecorder<Solution_> mimicRecordingValueSelector) {
        ValueMimicRecorder<Solution_> put = valueMimicRecorderMap.put(id, mimicRecordingValueSelector);
        if (put != null) {
            throw new IllegalStateException("Multiple " + ValueMimicRecorder.class.getSimpleName() + "s (usually "
                    + ValueSelector.class.getSimpleName() + "s) have the same id (" + id + ").");
        }
    }

    public ValueMimicRecorder<Solution_> getValueMimicRecorder(String id) {
        return valueMimicRecorderMap.get(id);
    }

    public ThreadFactory buildThreadFactory(ChildThreadType childThreadType) {
        if (threadFactoryClass != null) {
            return ConfigUtils.newInstance(this::toString, "threadFactoryClass", threadFactoryClass);
        } else {
            String threadPrefix;
            switch (childThreadType) {
                case MOVE_THREAD:
                    threadPrefix = "MoveThread";
                    break;
                case PART_THREAD:
                    threadPrefix = "PartThread";
                    break;
                default:
                    throw new IllegalStateException("Unsupported childThreadType (" + childThreadType + ").");
            }
            return new DefaultSolverThreadFactory(threadPrefix);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + environmentMode + ")";
    }

    public static class Builder<Solution_> {

        private final EnvironmentMode environmentMode;
        private final Integer moveThreadCount;
        private final Integer moveThreadBufferSize;
        private final Class<? extends ThreadFactory> threadFactoryClass;
        private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;

        private String logIndentation = "";

        private EntitySorterManner entitySorterManner = EntitySorterManner.NONE;
        private ValueSorterManner valueSorterManner = ValueSorterManner.NONE;

        private boolean reinitializeVariableFilterEnabled = false;
        private boolean initializedChainedValueFilterEnabled = false;

        public Builder(EnvironmentMode environmentMode, Integer moveThreadCount,
                Integer moveThreadBufferSize, Class<? extends ThreadFactory> threadFactoryClass,
                InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory) {
            this.environmentMode = environmentMode;
            this.moveThreadCount = moveThreadCount;
            this.moveThreadBufferSize = moveThreadBufferSize;
            this.threadFactoryClass = threadFactoryClass;
            this.scoreDirectorFactory = scoreDirectorFactory;
        }

        public Builder<Solution_> withLogIndentation(String logIndentation) {
            this.logIndentation = logIndentation;
            return this;
        }

        public Builder<Solution_> withEntitySorterManner(EntitySorterManner entitySorterManner) {
            this.entitySorterManner = entitySorterManner;
            return this;
        }

        public Builder<Solution_> withValueSorterManner(ValueSorterManner valueSorterManner) {
            this.valueSorterManner = valueSorterManner;
            return this;
        }

        public Builder<Solution_> withReinitializeVariableFilterEnabled(boolean reinitializeVariableFilterEnabled) {
            this.reinitializeVariableFilterEnabled = reinitializeVariableFilterEnabled;
            return this;
        }

        public Builder<Solution_> withInitializedChainedValueFilterEnabled(boolean initializedChainedValueFilterEnabled) {
            this.initializedChainedValueFilterEnabled = initializedChainedValueFilterEnabled;
            return this;
        }

        public HeuristicConfigPolicy<Solution_> build() {
            return new HeuristicConfigPolicy<>(this);
        }
    }
}
