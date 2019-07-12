/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.localsearch.decider.acceptor;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.hillclimbing.HillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.EntityTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.SolutionTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.ValueTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.EntityRatioTabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.FixedTabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.ValueRatioTabuSizeStrategy;

import static org.apache.commons.lang3.ObjectUtils.*;

@XStreamAlias("acceptor")
public class AcceptorConfig extends AbstractConfig<AcceptorConfig> {

    @Deprecated // TODO remove in 8.0
    @XStreamImplicit(itemFieldName = "acceptorClass")
    private List<Class<? extends Acceptor>> acceptorClassList = null;

    @XStreamImplicit(itemFieldName = "acceptorType")
    private List<AcceptorType> acceptorTypeList = null;

    protected Integer entityTabuSize = null;
    protected Double entityTabuRatio = null;
    protected Integer fadingEntityTabuSize = null;
    protected Double fadingEntityTabuRatio = null;
    protected Integer valueTabuSize = null;
    protected Double valueTabuRatio = null;
    protected Integer fadingValueTabuSize = null;
    protected Double fadingValueTabuRatio = null;
    protected Integer moveTabuSize = null;
    protected Integer fadingMoveTabuSize = null;
    protected Integer undoMoveTabuSize = null;
    protected Integer fadingUndoMoveTabuSize = null;
    protected Integer solutionTabuSize = null;
    protected Integer fadingSolutionTabuSize = null;

    protected String simulatedAnnealingStartingTemperature = null;

    protected Integer lateAcceptanceSize = null;

//    protected String greatDelugeInitialWaterLevel = null;
    protected String greatDelugeWaterLevelIncrementScore = null;
    protected Double greatDelugeWaterLevelIncrementRatio = null;

    protected Integer stepCountingHillClimbingSize = null;
    protected StepCountingHillClimbingType stepCountingHillClimbingType = null;

    @Deprecated
    public List<Class<? extends Acceptor>> getAcceptorClassList() {
        return acceptorClassList;
    }

    @Deprecated
    public void setAcceptorClassList(List<Class<? extends Acceptor>> acceptorClassList) {
        this.acceptorClassList = acceptorClassList;
    }

    public List<AcceptorType> getAcceptorTypeList() {
        return acceptorTypeList;
    }

    public void setAcceptorTypeList(List<AcceptorType> acceptorTypeList) {
        this.acceptorTypeList = acceptorTypeList;
    }

    public Integer getEntityTabuSize() {
        return entityTabuSize;
    }

    public void setEntityTabuSize(Integer entityTabuSize) {
        this.entityTabuSize = entityTabuSize;
    }

    public Double getEntityTabuRatio() {
        return entityTabuRatio;
    }

    public void setEntityTabuRatio(Double entityTabuRatio) {
        this.entityTabuRatio = entityTabuRatio;
    }

    public Integer getFadingEntityTabuSize() {
        return fadingEntityTabuSize;
    }

    public void setFadingEntityTabuSize(Integer fadingEntityTabuSize) {
        this.fadingEntityTabuSize = fadingEntityTabuSize;
    }

    public Double getFadingEntityTabuRatio() {
        return fadingEntityTabuRatio;
    }

    public void setFadingEntityTabuRatio(Double fadingEntityTabuRatio) {
        this.fadingEntityTabuRatio = fadingEntityTabuRatio;
    }

    public Integer getValueTabuSize() {
        return valueTabuSize;
    }

    public void setValueTabuSize(Integer valueTabuSize) {
        this.valueTabuSize = valueTabuSize;
    }

    public Double getValueTabuRatio() {
        return valueTabuRatio;
    }

    public void setValueTabuRatio(Double valueTabuRatio) {
        this.valueTabuRatio = valueTabuRatio;
    }

    public Integer getFadingValueTabuSize() {
        return fadingValueTabuSize;
    }

    public void setFadingValueTabuSize(Integer fadingValueTabuSize) {
        this.fadingValueTabuSize = fadingValueTabuSize;
    }

    public Double getFadingValueTabuRatio() {
        return fadingValueTabuRatio;
    }

    public void setFadingValueTabuRatio(Double fadingValueTabuRatio) {
        this.fadingValueTabuRatio = fadingValueTabuRatio;
    }

    public Integer getMoveTabuSize() {
        return moveTabuSize;
    }

    public void setMoveTabuSize(Integer moveTabuSize) {
        this.moveTabuSize = moveTabuSize;
    }

    public Integer getFadingMoveTabuSize() {
        return fadingMoveTabuSize;
    }

    public void setFadingMoveTabuSize(Integer fadingMoveTabuSize) {
        this.fadingMoveTabuSize = fadingMoveTabuSize;
    }

    public Integer getUndoMoveTabuSize() {
        return undoMoveTabuSize;
    }

    public void setUndoMoveTabuSize(Integer undoMoveTabuSize) {
        this.undoMoveTabuSize = undoMoveTabuSize;
    }

    public Integer getFadingUndoMoveTabuSize() {
        return fadingUndoMoveTabuSize;
    }

    public void setFadingUndoMoveTabuSize(Integer fadingUndoMoveTabuSize) {
        this.fadingUndoMoveTabuSize = fadingUndoMoveTabuSize;
    }

    public Integer getSolutionTabuSize() {
        return solutionTabuSize;
    }

    public void setSolutionTabuSize(Integer solutionTabuSize) {
        this.solutionTabuSize = solutionTabuSize;
    }

    public Integer getFadingSolutionTabuSize() {
        return fadingSolutionTabuSize;
    }

    public void setFadingSolutionTabuSize(Integer fadingSolutionTabuSize) {
        this.fadingSolutionTabuSize = fadingSolutionTabuSize;
    }

    public String getSimulatedAnnealingStartingTemperature() {
        return simulatedAnnealingStartingTemperature;
    }

    public void setSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
    }

    public Integer getLateAcceptanceSize() {
        return lateAcceptanceSize;
    }

    public void setLateAcceptanceSize(Integer lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
    }

//    public String getGreatDelugeInitialWaterLevel() {
//        return greatDelugeInitialWaterLevel;
//    }
//
//    public void setGreatDelugeInitialWaterLevel(String greatDelugeInitialWaterLevel) {
//        this.greatDelugeInitialWaterLevel = greatDelugeInitialWaterLevel;
//    }

    public String getGreatDelugeWaterLevelIncrementScore() {
        return greatDelugeWaterLevelIncrementScore;
    }

    public void setGreatDelugeWaterLevelIncrementScore(String greatDelugeWaterLevelIncrementScore) {
        this.greatDelugeWaterLevelIncrementScore = greatDelugeWaterLevelIncrementScore;
    }

    public Double getGreatDelugeWaterLevelIncrementRatio() {
        return greatDelugeWaterLevelIncrementRatio;
    }

    public void setGreatDelugeWaterLevelIncrementRatio(Double greatDelugeWaterLevelIncrementRatio) {
        this.greatDelugeWaterLevelIncrementRatio = greatDelugeWaterLevelIncrementRatio;
    }

    public Integer getStepCountingHillClimbingSize() {
        return stepCountingHillClimbingSize;
    }

    public void setStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
    }

    public StepCountingHillClimbingType getStepCountingHillClimbingType() {
        return stepCountingHillClimbingType;
    }

    public void setStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    @Deprecated
    public AcceptorConfig withAcceptorClassList(List<Class<? extends Acceptor>> acceptorClassList) {
        this.acceptorClassList = acceptorClassList;
        return this;
    }

    public AcceptorConfig withAcceptorTypeList(List<AcceptorType> acceptorTypeList) {
        this.acceptorTypeList = acceptorTypeList;
        return this;
    }

    public AcceptorConfig withEntityTabuSize(Integer entityTabuSize) {
        this.entityTabuSize = entityTabuSize;
        return this;
    }

    public AcceptorConfig withEntityTabuRatio(Double entityTabuRatio) {
        this.entityTabuRatio = entityTabuRatio;
        return this;
    }

    public AcceptorConfig withFadingEntityTabuSize(Integer fadingEntityTabuSize) {
        this.fadingEntityTabuSize = fadingEntityTabuSize;
        return this;
    }

    public AcceptorConfig withFadingEntityTabuRatio(Double fadingEntityTabuRatio) {
        this.fadingEntityTabuRatio = fadingEntityTabuRatio;
        return this;
    }

    public AcceptorConfig withValueTabuSize(Integer valueTabuSize) {
        this.valueTabuSize = valueTabuSize;
        return this;
    }

    public AcceptorConfig withValueTabuRatio(Double valueTabuRatio) {
        this.valueTabuRatio = valueTabuRatio;
        return this;
    }

    public AcceptorConfig withFadingValueTabuSize(Integer fadingValueTabuSize) {
        this.fadingValueTabuSize = fadingValueTabuSize;
        return this;
    }

    public AcceptorConfig withFadingValueTabuRatio(Double fadingValueTabuRatio) {
        this.fadingValueTabuRatio = fadingValueTabuRatio;
        return this;
    }

    public AcceptorConfig withMoveTabuSize(Integer moveTabuSize) {
        this.moveTabuSize = moveTabuSize;
        return this;
    }

    public AcceptorConfig withFadingMoveTabuSize(Integer fadingMoveTabuSize) {
        this.fadingMoveTabuSize = fadingMoveTabuSize;
        return this;
    }

    public AcceptorConfig withUndoMoveTabuSize(Integer undoMoveTabuSize) {
        this.undoMoveTabuSize = undoMoveTabuSize;
        return this;
    }

    public AcceptorConfig withFadingUndoMoveTabuSize(Integer fadingUndoMoveTabuSize) {
        this.fadingUndoMoveTabuSize = fadingUndoMoveTabuSize;
        return this;
    }

    public AcceptorConfig withSolutionTabuSize(Integer solutionTabuSize) {
        this.solutionTabuSize = solutionTabuSize;
        return this;
    }

    public AcceptorConfig withFadingSolutionTabuSize(Integer fadingSolutionTabuSize) {
        this.fadingSolutionTabuSize = fadingSolutionTabuSize;
        return this;
    }

    public AcceptorConfig withSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
        return this;
    }

    public AcceptorConfig withLateAcceptanceSize(Integer lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
        return this;
    }

    public AcceptorConfig withStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
        return this;
    }

    public AcceptorConfig withStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Acceptor buildAcceptor(HeuristicConfigPolicy configPolicy) {
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        List<Acceptor> acceptorList = new ArrayList<>();
        if (acceptorClassList != null) {
            for (Class<? extends Acceptor> acceptorClass : acceptorClassList) {
                Acceptor acceptor = ConfigUtils.newInstance(this, "acceptorClass", acceptorClass);
                acceptorList.add(acceptor);
            }
        }
        if (acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.HILL_CLIMBING)) {
            HillClimbingAcceptor acceptor = new HillClimbingAcceptor();
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.ENTITY_TABU))
                || entityTabuSize != null || entityTabuRatio != null
                || fadingEntityTabuSize != null || fadingEntityTabuRatio != null) {
            EntityTabuAcceptor acceptor = new EntityTabuAcceptor(configPolicy.getLogIndentation());
            if (entityTabuSize != null) {
                if (entityTabuRatio != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both entityTabuSize ("
                            + entityTabuSize + ") and entityTabuRatio (" + entityTabuRatio + ").");
                }
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(entityTabuSize));
            } else if (entityTabuRatio != null) {
                acceptor.setTabuSizeStrategy(new EntityRatioTabuSizeStrategy(entityTabuRatio));
            } else if (fadingEntityTabuSize == null && fadingEntityTabuRatio == null) {
                acceptor.setTabuSizeStrategy(new EntityRatioTabuSizeStrategy(0.1));
            }
            if (fadingEntityTabuSize != null) {
                if (fadingEntityTabuRatio != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both fadingEntityTabuSize ("
                            + fadingEntityTabuSize + ") and fadingEntityTabuRatio ("
                            + fadingEntityTabuRatio + ").");
                }
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingEntityTabuSize));
            } else if (fadingEntityTabuRatio != null) {
                acceptor.setFadingTabuSizeStrategy(new EntityRatioTabuSizeStrategy(fadingEntityTabuRatio));
            }
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.VALUE_TABU))
                || valueTabuSize != null || valueTabuRatio != null
                || fadingValueTabuSize != null  || fadingValueTabuRatio != null) {
            ValueTabuAcceptor acceptor = new ValueTabuAcceptor(configPolicy.getLogIndentation());
            if (valueTabuSize != null) {
                if (valueTabuRatio != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both valueTabuSize ("
                            + valueTabuSize + ") and valueTabuRatio (" + valueTabuRatio + ").");
                }
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(valueTabuSize));
            } else if (valueTabuRatio != null) {
                acceptor.setTabuSizeStrategy(new ValueRatioTabuSizeStrategy(valueTabuRatio));
            }
            if (fadingValueTabuSize != null) {
                if (fadingValueTabuRatio != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both fadingValueTabuSize ("
                            + fadingValueTabuSize + ") and fadingValueTabuRatio ("
                            + fadingValueTabuRatio + ").");
                }
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingValueTabuSize));
            } else if (fadingValueTabuRatio != null) {
                acceptor.setFadingTabuSizeStrategy(new ValueRatioTabuSizeStrategy(fadingValueTabuRatio));
            }

            if (valueTabuSize != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(valueTabuSize));
            }
            if (fadingValueTabuSize != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingValueTabuSize));
            }
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.MOVE_TABU))
                || moveTabuSize != null || fadingMoveTabuSize != null) {
            MoveTabuAcceptor acceptor = new MoveTabuAcceptor(configPolicy.getLogIndentation());
            acceptor.setUseUndoMoveAsTabuMove(false);
            if (moveTabuSize != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(moveTabuSize));
            }
            if (fadingMoveTabuSize != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingMoveTabuSize));
            }
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.UNDO_MOVE_TABU))
                || undoMoveTabuSize != null || fadingUndoMoveTabuSize != null) {
            MoveTabuAcceptor acceptor = new MoveTabuAcceptor(configPolicy.getLogIndentation());
            acceptor.setUseUndoMoveAsTabuMove(true);
            if (undoMoveTabuSize != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(undoMoveTabuSize));
            }
            if (fadingUndoMoveTabuSize != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingUndoMoveTabuSize));
            }
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.SOLUTION_TABU))
                || solutionTabuSize != null || fadingSolutionTabuSize != null) {
            SolutionTabuAcceptor acceptor = new SolutionTabuAcceptor(configPolicy.getLogIndentation());
            if (solutionTabuSize != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy(solutionTabuSize));
            }
            if (fadingSolutionTabuSize != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy(fadingSolutionTabuSize));
            }
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.SIMULATED_ANNEALING))
                || simulatedAnnealingStartingTemperature != null) {
            SimulatedAnnealingAcceptor acceptor = new SimulatedAnnealingAcceptor();
            if (simulatedAnnealingStartingTemperature == null) {
                // TODO Support SA without a parameter
                throw new IllegalArgumentException("The acceptorType (" + AcceptorType.SIMULATED_ANNEALING
                        + ") currently requires a simulatedAnnealingStartingTemperature ("
                        + simulatedAnnealingStartingTemperature + ").");
            }
            acceptor.setStartingTemperature(
                    configPolicy.getScoreDefinition().parseScore(simulatedAnnealingStartingTemperature));
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.LATE_ACCEPTANCE))
                || lateAcceptanceSize != null) {
            LateAcceptanceAcceptor acceptor = new LateAcceptanceAcceptor();
            acceptor.setLateAcceptanceSize(defaultIfNull(lateAcceptanceSize, 400));
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList!= null && acceptorTypeList.contains(AcceptorType.GREAT_DELUGE))
//                || greatDelugeInitialWaterLevel != null
                || greatDelugeWaterLevelIncrementScore != null
                || greatDelugeWaterLevelIncrementRatio != null) {
            GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
//            if (greatDelugeInitialWaterLevel != null) {
//                acceptor.setInitialWaterLevel(
//                        configPolicy.getScoreDefinition().parseScore(greatDelugeInitialWaterLevel));
//            }
            if (greatDelugeWaterLevelIncrementScore != null) {
                if (greatDelugeWaterLevelIncrementRatio != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both a "
                            + "greatDelugeWaterLevelIncrementScore (" + greatDelugeWaterLevelIncrementScore
                            + ") and a greatDelugeWaterLevelIncrementRatio (" + greatDelugeWaterLevelIncrementRatio + ").");
                }
                acceptor.setWaterLevelIncrementScore(
                        configPolicy.getScoreDefinition().parseScore(greatDelugeWaterLevelIncrementScore));
            } else if (greatDelugeWaterLevelIncrementRatio != null) {
                if (greatDelugeWaterLevelIncrementRatio <= 0.0) {
                    throw new IllegalArgumentException("The greatDelugeWaterLevelIncrementRatio ("
                            + greatDelugeWaterLevelIncrementRatio
                            + ") must be positive because the water level should increase.");
                }
                acceptor.setWaterLevelIncrementRatio(greatDelugeWaterLevelIncrementRatio);
            } else {
                // Based on Tomas Muller's work. TODO Confirm with benchmarker across our examples/datasets
                acceptor.setWaterLevelIncrementRatio(0.00_000_005);
            }
            acceptorList.add(acceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.STEP_COUNTING_HILL_CLIMBING))
                || stepCountingHillClimbingSize != null) {
            int stepCountingHillClimbingSize_ = defaultIfNull(stepCountingHillClimbingSize, 400);
            StepCountingHillClimbingType stepCountingHillClimbingType_
                    = defaultIfNull(stepCountingHillClimbingType, StepCountingHillClimbingType.STEP);
            StepCountingHillClimbingAcceptor acceptor = new StepCountingHillClimbingAcceptor(
                    stepCountingHillClimbingSize_, stepCountingHillClimbingType_);
            acceptorList.add(acceptor);
        }

        if (acceptorList.size() == 1) {
            return acceptorList.get(0);
        } else if (acceptorList.size() > 1) {
            return new CompositeAcceptor(acceptorList);
        } else {
            throw new IllegalArgumentException("The acceptor does not specify any acceptorType (" + acceptorTypeList
                    + ") or other acceptor property.\n"
                    + "For a good starting values,"
                    + " see the docs section \"Which optimization algorithms should I use?\".");
        }
    }

    @Override
    public void inherit(AcceptorConfig inheritedConfig) {
        acceptorClassList = ConfigUtils.inheritMergeableListProperty(acceptorClassList,
                inheritedConfig.getAcceptorClassList());
        if (acceptorTypeList == null) {
            acceptorTypeList = inheritedConfig.getAcceptorTypeList();
        } else {
            List<AcceptorType> inheritedAcceptorTypeList = inheritedConfig.getAcceptorTypeList();
            if (inheritedAcceptorTypeList != null) {
                for (AcceptorType acceptorType : inheritedAcceptorTypeList) {
                    if (!acceptorTypeList.contains(acceptorType)) {
                        acceptorTypeList.add(acceptorType);
                    }
                }
            }
        }
        entityTabuSize = ConfigUtils.inheritOverwritableProperty(entityTabuSize, inheritedConfig.getEntityTabuSize());
        entityTabuRatio = ConfigUtils.inheritOverwritableProperty(entityTabuRatio, inheritedConfig.getEntityTabuRatio());
        fadingEntityTabuSize = ConfigUtils.inheritOverwritableProperty(fadingEntityTabuSize,
                inheritedConfig.getFadingEntityTabuSize());
        fadingEntityTabuRatio = ConfigUtils.inheritOverwritableProperty(fadingEntityTabuRatio,
                inheritedConfig.getFadingEntityTabuRatio());
        valueTabuSize = ConfigUtils.inheritOverwritableProperty(valueTabuSize, inheritedConfig.getValueTabuSize());
        valueTabuRatio = ConfigUtils.inheritOverwritableProperty(valueTabuRatio, inheritedConfig.getValueTabuRatio());
        fadingValueTabuSize = ConfigUtils.inheritOverwritableProperty(fadingValueTabuSize,
                inheritedConfig.getFadingValueTabuSize());
        fadingValueTabuRatio = ConfigUtils.inheritOverwritableProperty(fadingValueTabuRatio,
                inheritedConfig.getFadingValueTabuRatio());
        moveTabuSize = ConfigUtils.inheritOverwritableProperty(moveTabuSize, inheritedConfig.getMoveTabuSize());
        fadingMoveTabuSize = ConfigUtils.inheritOverwritableProperty(fadingMoveTabuSize,
                inheritedConfig.getFadingMoveTabuSize());
        undoMoveTabuSize = ConfigUtils.inheritOverwritableProperty(undoMoveTabuSize,
                inheritedConfig.getUndoMoveTabuSize());
        fadingUndoMoveTabuSize = ConfigUtils.inheritOverwritableProperty(fadingUndoMoveTabuSize,
                inheritedConfig.getFadingUndoMoveTabuSize());
        solutionTabuSize = ConfigUtils.inheritOverwritableProperty(solutionTabuSize,
                inheritedConfig.getSolutionTabuSize());
        fadingSolutionTabuSize = ConfigUtils.inheritOverwritableProperty(fadingSolutionTabuSize,
                inheritedConfig.getFadingSolutionTabuSize());
        simulatedAnnealingStartingTemperature = ConfigUtils.inheritOverwritableProperty(
                simulatedAnnealingStartingTemperature, inheritedConfig.getSimulatedAnnealingStartingTemperature());
        lateAcceptanceSize = ConfigUtils.inheritOverwritableProperty(lateAcceptanceSize,
                inheritedConfig.getLateAcceptanceSize());
//        greatDelugeInitialWaterLevel = ConfigUtils.inheritOverwritableProperty(greatDelugeInitialWaterLevel,
//                inheritedConfig.getGreatDelugeInitialWaterLevel());
        greatDelugeWaterLevelIncrementScore = ConfigUtils.inheritOverwritableProperty(greatDelugeWaterLevelIncrementScore,
                inheritedConfig.getGreatDelugeWaterLevelIncrementScore());
        greatDelugeWaterLevelIncrementRatio = ConfigUtils.inheritOverwritableProperty(greatDelugeWaterLevelIncrementRatio,
                inheritedConfig.getGreatDelugeWaterLevelIncrementRatio());
        stepCountingHillClimbingSize = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingSize,
                inheritedConfig.getStepCountingHillClimbingSize());
        stepCountingHillClimbingType = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingType,
                inheritedConfig.getStepCountingHillClimbingType());

    }

}
