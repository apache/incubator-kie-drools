/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.config.localsearch.decider.acceptor;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang.ObjectUtils;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.acceptor.CompositeAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.PlanningEntityTabuAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.PlanningValueTabuAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.SolutionTabuAcceptor;
import org.drools.planner.core.score.definition.ScoreDefinition;

@XStreamAlias("acceptor")
public class AcceptorConfig {

    @XStreamImplicit(itemFieldName = "acceptorClass")
    private List<Class<? extends Acceptor>> acceptorClassList = null; // TODO make into a list

    @XStreamImplicit(itemFieldName = "acceptorType")
    private List<AcceptorType> acceptorTypeList = null;

    protected Integer moveTabuSize = null;
    protected Integer partialMoveTabuSize = null;
    protected Integer undoMoveTabuSize = null;
    protected Integer partialUndoMoveTabuSize = null;
    protected Integer planningEntityTabuSize = null;
    protected Integer partialPlanningEntityTabuSize = null;
    protected Integer planningValueTabuSize = null;
    protected Integer partialPlanningValueTabuSize = null;
    protected Integer solutionTabuSize = null;
    protected Integer partialSolutionTabuSize = null;

    protected String simulatedAnnealingStartingTemperature = null;

    protected Double greatDelugeWaterLevelUpperBoundRate = null;
    protected Double greatDelugeWaterRisingRate = null;

    protected Integer lateAcceptanceSize = null;

    public List<Class<? extends Acceptor>> getAcceptorClassList() {
        return acceptorClassList;
    }

    public void setAcceptorClassList(List<Class<? extends Acceptor>> acceptorClassList) {
        this.acceptorClassList = acceptorClassList;
    }

    public List<AcceptorType> getAcceptorTypeList() {
        return acceptorTypeList;
    }

    public void setAcceptorTypeList(List<AcceptorType> acceptorTypeList) {
        this.acceptorTypeList = acceptorTypeList;
    }

    public Integer getMoveTabuSize() {
        return moveTabuSize;
    }

    public void setMoveTabuSize(Integer moveTabuSize) {
        this.moveTabuSize = moveTabuSize;
    }

    public Integer getPartialMoveTabuSize() {
        return partialMoveTabuSize;
    }

    public void setPartialMoveTabuSize(Integer partialMoveTabuSize) {
        this.partialMoveTabuSize = partialMoveTabuSize;
    }

    public Integer getUndoMoveTabuSize() {
        return undoMoveTabuSize;
    }

    public void setUndoMoveTabuSize(Integer undoMoveTabuSize) {
        this.undoMoveTabuSize = undoMoveTabuSize;
    }

    public Integer getPartialUndoMoveTabuSize() {
        return partialUndoMoveTabuSize;
    }

    public void setPartialUndoMoveTabuSize(Integer partialUndoMoveTabuSize) {
        this.partialUndoMoveTabuSize = partialUndoMoveTabuSize;
    }

    public Integer getPlanningEntityTabuSize() {
        return planningEntityTabuSize;
    }

    public void setPlanningEntityTabuSize(Integer planningEntityTabuSize) {
        this.planningEntityTabuSize = planningEntityTabuSize;
    }

    public Integer getPartialPlanningEntityTabuSize() {
        return partialPlanningEntityTabuSize;
    }

    public void setPartialPlanningEntityTabuSize(Integer partialPlanningEntityTabuSize) {
        this.partialPlanningEntityTabuSize = partialPlanningEntityTabuSize;
    }

    public Integer getPlanningValueTabuSize() {
        return planningValueTabuSize;
    }

    public void setPlanningValueTabuSize(Integer planningValueTabuSize) {
        this.planningValueTabuSize = planningValueTabuSize;
    }

    public Integer getPartialPlanningValueTabuSize() {
        return partialPlanningValueTabuSize;
    }

    public void setPartialPlanningValueTabuSize(Integer partialPlanningValueTabuSize) {
        this.partialPlanningValueTabuSize = partialPlanningValueTabuSize;
    }

    public Integer getSolutionTabuSize() {
        return solutionTabuSize;
    }

    public void setSolutionTabuSize(Integer solutionTabuSize) {
        this.solutionTabuSize = solutionTabuSize;
    }

    public Integer getPartialSolutionTabuSize() {
        return partialSolutionTabuSize;
    }

    public void setPartialSolutionTabuSize(Integer partialSolutionTabuSize) {
        this.partialSolutionTabuSize = partialSolutionTabuSize;
    }

    public String getSimulatedAnnealingStartingTemperature() {
        return simulatedAnnealingStartingTemperature;
    }

    public void setSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
    }

    public Double getGreatDelugeWaterLevelUpperBoundRate() {
        return greatDelugeWaterLevelUpperBoundRate;
    }

    public void setGreatDelugeWaterLevelUpperBoundRate(Double greatDelugeWaterLevelUpperBoundRate) {
        this.greatDelugeWaterLevelUpperBoundRate = greatDelugeWaterLevelUpperBoundRate;
    }

    public Double getGreatDelugeWaterRisingRate() {
        return greatDelugeWaterRisingRate;
    }

    public void setGreatDelugeWaterRisingRate(Double greatDelugeWaterRisingRate) {
        this.greatDelugeWaterRisingRate = greatDelugeWaterRisingRate;
    }

    public Integer getLateAcceptanceSize() {
        return lateAcceptanceSize;
    }

    public void setLateAcceptanceSize(Integer lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Acceptor buildAcceptor(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition) {
        List<Acceptor> acceptorList = new ArrayList<Acceptor>();
        if (acceptorClassList != null) {
            for (Class<? extends Acceptor> acceptorClass : acceptorClassList) {
                try {
                    acceptorList.add(acceptorClass.newInstance());
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException("acceptorClass (" + acceptorClass.getName()
                            + ") does not have a public no-arg constructor", e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("acceptorClass (" + acceptorClass.getName()
                            + ") does not have a public no-arg constructor", e);
                }
            }
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.MOVE_TABU))
                || moveTabuSize != null || partialMoveTabuSize != null) {
            MoveTabuAcceptor moveTabuAcceptor = new MoveTabuAcceptor();
            moveTabuAcceptor.setUseUndoMoveAsTabuMove(false);
            if (moveTabuSize != null) {
                moveTabuAcceptor.setTabuSize(moveTabuSize);
            }
            if (partialMoveTabuSize != null) {
                moveTabuAcceptor.setPartialTabuSize(partialMoveTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                moveTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(moveTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.UNDO_MOVE_TABU))
                || undoMoveTabuSize != null || partialUndoMoveTabuSize != null) {
            MoveTabuAcceptor undoMoveTabuAcceptor = new MoveTabuAcceptor();
            undoMoveTabuAcceptor.setUseUndoMoveAsTabuMove(true);
            if (undoMoveTabuSize != null) {
                undoMoveTabuAcceptor.setTabuSize(undoMoveTabuSize);
            }
            if (partialUndoMoveTabuSize != null) {
                undoMoveTabuAcceptor.setPartialTabuSize(partialUndoMoveTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                undoMoveTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(undoMoveTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.PLANNING_ENTITY_TABU))
                || planningEntityTabuSize != null || partialPlanningEntityTabuSize != null) {
            PlanningEntityTabuAcceptor planningEntityTabuAcceptor = new PlanningEntityTabuAcceptor();
            if (planningEntityTabuSize != null) {
                planningEntityTabuAcceptor.setTabuSize(planningEntityTabuSize);
            }
            if (partialPlanningEntityTabuSize != null) {
                planningEntityTabuAcceptor.setPartialTabuSize(partialPlanningEntityTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                planningEntityTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(planningEntityTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.PLANNING_VALUE_TABU))
                || planningValueTabuSize != null || partialPlanningValueTabuSize != null) {
            PlanningValueTabuAcceptor planningValueTabuAcceptor = new PlanningValueTabuAcceptor();
            if (planningValueTabuSize != null) {
                planningValueTabuAcceptor.setTabuSize(planningValueTabuSize);
            }
            if (partialPlanningValueTabuSize != null) {
                planningValueTabuAcceptor.setPartialTabuSize(partialPlanningValueTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                planningValueTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(planningValueTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.SOLUTION_TABU))
                || solutionTabuSize != null || partialSolutionTabuSize != null) {
            SolutionTabuAcceptor solutionTabuAcceptor = new SolutionTabuAcceptor();
            if (solutionTabuSize != null) {
                solutionTabuAcceptor.setTabuSize(solutionTabuSize);
            }
            if (partialSolutionTabuSize != null) {
                solutionTabuAcceptor.setPartialTabuSize(partialSolutionTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                solutionTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(solutionTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.SIMULATED_ANNEALING))
                || simulatedAnnealingStartingTemperature != null) {
            SimulatedAnnealingAcceptor simulatedAnnealingAcceptor = new SimulatedAnnealingAcceptor();
            simulatedAnnealingAcceptor.setStartingTemperature(scoreDefinition.parseScore(simulatedAnnealingStartingTemperature));
            acceptorList.add(simulatedAnnealingAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.GREAT_DELUGE))
                || greatDelugeWaterLevelUpperBoundRate != null || greatDelugeWaterRisingRate != null) {
            double waterLevelUpperBoundRate = (Double) ObjectUtils.defaultIfNull(
                    greatDelugeWaterLevelUpperBoundRate, 1.20);
            double waterRisingRate = (Double) ObjectUtils.defaultIfNull(
                    greatDelugeWaterRisingRate, 0.0000001);
            acceptorList.add(new GreatDelugeAcceptor(waterLevelUpperBoundRate, waterRisingRate));
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.LATE_ACCEPTANCE))
                || lateAcceptanceSize != null ) {
            LateAcceptanceAcceptor lateAcceptanceAcceptor = new LateAcceptanceAcceptor();
            lateAcceptanceAcceptor.setLateAcceptanceSize((lateAcceptanceSize == null) ? 1000 : lateAcceptanceSize);
            acceptorList.add(lateAcceptanceAcceptor);
        }
        if (acceptorList.size() == 1) {
            return acceptorList.get(0);
        } else if (acceptorList.size() > 1) {
            CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
            compositeAcceptor.setAcceptorList(acceptorList);
            return compositeAcceptor;
        } else {
            SolutionTabuAcceptor solutionTabuAcceptor = new SolutionTabuAcceptor();
            solutionTabuAcceptor.setTabuSize(1500); // TODO number pulled out of thin air
            if (environmentMode == EnvironmentMode.TRACE) {
                solutionTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return solutionTabuAcceptor;
        }
    }

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
        if (moveTabuSize == null) {
            moveTabuSize = inheritedConfig.getMoveTabuSize();
        }
        if (partialMoveTabuSize == null) {
            partialMoveTabuSize = inheritedConfig.getPartialMoveTabuSize();
        }
        if (undoMoveTabuSize == null) {
            undoMoveTabuSize = inheritedConfig.getUndoMoveTabuSize();
        }
        if (partialUndoMoveTabuSize == null) {
            partialUndoMoveTabuSize = inheritedConfig.getPartialUndoMoveTabuSize();
        }
        if (planningEntityTabuSize == null) {
            planningEntityTabuSize = inheritedConfig.getPlanningEntityTabuSize();
        }
        if (partialPlanningEntityTabuSize == null) {
            partialPlanningEntityTabuSize = inheritedConfig.getPartialPlanningEntityTabuSize();
        }
        if (solutionTabuSize == null) {
            solutionTabuSize = inheritedConfig.getSolutionTabuSize();
        }
        if (partialSolutionTabuSize == null) {
            partialSolutionTabuSize = inheritedConfig.getPartialSolutionTabuSize();
        }
        if (simulatedAnnealingStartingTemperature == null) {
            simulatedAnnealingStartingTemperature = inheritedConfig.getSimulatedAnnealingStartingTemperature();
        }
        if (greatDelugeWaterLevelUpperBoundRate == null) {
            greatDelugeWaterLevelUpperBoundRate = inheritedConfig.getGreatDelugeWaterLevelUpperBoundRate();
        }
        if (greatDelugeWaterRisingRate == null) {
            greatDelugeWaterRisingRate = inheritedConfig.getGreatDelugeWaterRisingRate();
        }
        if (lateAcceptanceSize == null) {
            lateAcceptanceSize = inheritedConfig.getLateAcceptanceSize();
        }
    }

    public static enum AcceptorType {
        PLANNING_ENTITY_TABU,
        PLANNING_VALUE_TABU,
        MOVE_TABU,
        UNDO_MOVE_TABU,
        SOLUTION_TABU,
        SIMULATED_ANNEALING,
        GREAT_DELUGE,
        LATE_ACCEPTANCE,
    }

}
