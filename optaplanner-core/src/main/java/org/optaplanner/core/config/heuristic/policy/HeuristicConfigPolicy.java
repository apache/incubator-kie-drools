/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.heuristic.policy;

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

public class HeuristicConfigPolicy {

    private final EnvironmentMode environmentMode;
    private final String logIndentation;
    private final Integer moveThreadCount;
    private final Integer moveThreadBufferSize;
    private final Class<? extends ThreadFactory> threadFactoryClass;
    private final InnerScoreDirectorFactory scoreDirectorFactory;

    private EntitySorterManner entitySorterManner = EntitySorterManner.NONE;
    private ValueSorterManner valueSorterManner = ValueSorterManner.NONE;
    private boolean reinitializeVariableFilterEnabled = false;
    private boolean initializedChainedValueFilterEnabled = false;

    private Map<String, EntityMimicRecorder> entityMimicRecorderMap
            = new HashMap<>();
    private Map<String, ValueMimicRecorder> valueMimicRecorderMap
            = new HashMap<>();

    public HeuristicConfigPolicy(EnvironmentMode environmentMode,
            Integer moveThreadCount, Integer moveThreadBufferSize, Class<? extends ThreadFactory> threadFactoryClass,
            InnerScoreDirectorFactory scoreDirectorFactory) {
        this(environmentMode, "", moveThreadCount, moveThreadBufferSize, threadFactoryClass, scoreDirectorFactory);
    }

    public HeuristicConfigPolicy(EnvironmentMode environmentMode, String logIndentation,
            Integer moveThreadCount, Integer moveThreadBufferSize,
            Class<? extends ThreadFactory> threadFactoryClass, InnerScoreDirectorFactory scoreDirectorFactory) {
        this.environmentMode = environmentMode;
        this.logIndentation = logIndentation;
        this.moveThreadCount = moveThreadCount;
        this.moveThreadBufferSize = moveThreadBufferSize;
        this.threadFactoryClass = threadFactoryClass;
        this.scoreDirectorFactory = scoreDirectorFactory;
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

    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    public Integer getMoveThreadBufferSize() {
        return moveThreadBufferSize;
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

    public Map<String, ValueMimicRecorder> getValueMimicRecorderMap() {
        return valueMimicRecorderMap;
    }

    public void setValueMimicRecorderMap(Map<String, ValueMimicRecorder> valueMimicRecorderMap) {
        this.valueMimicRecorderMap = valueMimicRecorderMap;
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
        return new HeuristicConfigPolicy(environmentMode, logIndentation,
                moveThreadCount, moveThreadBufferSize, threadFactoryClass,
                scoreDirectorFactory);
    }

    public HeuristicConfigPolicy createChildThreadConfigPolicy(ChildThreadType childThreadType) {
        return new HeuristicConfigPolicy(environmentMode, logIndentation + "        ",
                moveThreadCount, moveThreadBufferSize, threadFactoryClass,
                scoreDirectorFactory);
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

    public void addValueMimicRecorder(String id, ValueMimicRecorder mimicRecordingValueSelector) {
        ValueMimicRecorder put = valueMimicRecorderMap.put(id, mimicRecordingValueSelector);
        if (put != null) {
            throw new IllegalStateException("Multiple " + ValueMimicRecorder.class.getSimpleName() + "s (usually "
                    + ValueSelector.class.getSimpleName() + "s) have the same id (" + id + ").");
        }
    }

    public ValueMimicRecorder getValueMimicRecorder(String id) {
        return valueMimicRecorderMap.get(id);
    }

    public ThreadFactory buildThreadFactory(ChildThreadType childThreadType) {
        if (threadFactoryClass != null) {
            return ConfigUtils.newInstance(this, "threadFactoryClass", threadFactoryClass);
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
}
