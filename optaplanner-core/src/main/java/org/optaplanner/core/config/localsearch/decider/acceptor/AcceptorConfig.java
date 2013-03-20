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

package org.optaplanner.core.config.localsearch.decider.acceptor;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.PlanningEntityTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.PlanningValueTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.SolutionTabuAcceptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

@XStreamAlias("acceptor")
public class AcceptorConfig {

    @XStreamImplicit(itemFieldName = "acceptorClass")
    private List<Class<? extends Acceptor>> acceptorClassList = null; // TODO make into a list

    @XStreamImplicit(itemFieldName = "acceptorType")
    private List<AcceptorType> acceptorTypeList = null;

    protected Integer planningEntityTabuSize = null;
    protected Integer fadingPlanningEntityTabuSize = null;
    protected Integer planningValueTabuSize = null;
    protected Integer fadingPlanningValueTabuSize = null;
    protected Integer moveTabuSize = null;
    protected Integer fadingMoveTabuSize = null;
    protected Integer undoMoveTabuSize = null;
    protected Integer fadingUndoMoveTabuSize = null;
    protected Integer solutionTabuSize = null;
    protected Integer fadingSolutionTabuSize = null;

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

    public Integer getPlanningEntityTabuSize() {
        return planningEntityTabuSize;
    }

    public void setPlanningEntityTabuSize(Integer planningEntityTabuSize) {
        this.planningEntityTabuSize = planningEntityTabuSize;
    }

    public Integer getFadingPlanningEntityTabuSize() {
        return fadingPlanningEntityTabuSize;
    }

    public void setFadingPlanningEntityTabuSize(Integer fadingPlanningEntityTabuSize) {
        this.fadingPlanningEntityTabuSize = fadingPlanningEntityTabuSize;
    }

    public Integer getPlanningValueTabuSize() {
        return planningValueTabuSize;
    }

    public void setPlanningValueTabuSize(Integer planningValueTabuSize) {
        this.planningValueTabuSize = planningValueTabuSize;
    }

    public Integer getFadingPlanningValueTabuSize() {
        return fadingPlanningValueTabuSize;
    }

    public void setFadingPlanningValueTabuSize(Integer fadingPlanningValueTabuSize) {
        this.fadingPlanningValueTabuSize = fadingPlanningValueTabuSize;
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
                Acceptor acceptor = ConfigUtils.newInstance(this, "acceptorClass", acceptorClass);
                acceptorList.add(acceptor);
            }
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.PLANNING_ENTITY_TABU))
                || planningEntityTabuSize != null || fadingPlanningEntityTabuSize != null) {
            PlanningEntityTabuAcceptor planningEntityTabuAcceptor = new PlanningEntityTabuAcceptor();
            if (planningEntityTabuSize != null) {
                planningEntityTabuAcceptor.setTabuSize(planningEntityTabuSize);
            }
            if (fadingPlanningEntityTabuSize != null) {
                planningEntityTabuAcceptor.setFadingTabuSize(fadingPlanningEntityTabuSize);
            }
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
                planningEntityTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(planningEntityTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.PLANNING_VALUE_TABU))
                || planningValueTabuSize != null || fadingPlanningValueTabuSize != null) {
            PlanningValueTabuAcceptor planningValueTabuAcceptor = new PlanningValueTabuAcceptor();
            if (planningValueTabuSize != null) {
                planningValueTabuAcceptor.setTabuSize(planningValueTabuSize);
            }
            if (fadingPlanningValueTabuSize != null) {
                planningValueTabuAcceptor.setFadingTabuSize(fadingPlanningValueTabuSize);
            }
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
                planningValueTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(planningValueTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.MOVE_TABU))
                || moveTabuSize != null || fadingMoveTabuSize != null) {
            MoveTabuAcceptor moveTabuAcceptor = new MoveTabuAcceptor();
            moveTabuAcceptor.setUseUndoMoveAsTabuMove(false);
            if (moveTabuSize != null) {
                moveTabuAcceptor.setTabuSize(moveTabuSize);
            }
            if (fadingMoveTabuSize != null) {
                moveTabuAcceptor.setFadingTabuSize(fadingMoveTabuSize);
            }
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
                moveTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(moveTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.UNDO_MOVE_TABU))
                || undoMoveTabuSize != null || fadingUndoMoveTabuSize != null) {
            MoveTabuAcceptor undoMoveTabuAcceptor = new MoveTabuAcceptor();
            undoMoveTabuAcceptor.setUseUndoMoveAsTabuMove(true);
            if (undoMoveTabuSize != null) {
                undoMoveTabuAcceptor.setTabuSize(undoMoveTabuSize);
            }
            if (fadingUndoMoveTabuSize != null) {
                undoMoveTabuAcceptor.setFadingTabuSize(fadingUndoMoveTabuSize);
            }
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
                undoMoveTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(undoMoveTabuAcceptor);
        }
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.SOLUTION_TABU))
                || solutionTabuSize != null || fadingSolutionTabuSize != null) {
            SolutionTabuAcceptor solutionTabuAcceptor = new SolutionTabuAcceptor();
            if (solutionTabuSize != null) {
                solutionTabuAcceptor.setTabuSize(solutionTabuSize);
            }
            if (fadingSolutionTabuSize != null) {
                solutionTabuAcceptor.setFadingTabuSize(fadingSolutionTabuSize);
            }
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
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
            PlanningEntityTabuAcceptor planningEntityTabuAcceptor = new PlanningEntityTabuAcceptor();
            planningEntityTabuAcceptor.setTabuSize(5); // TODO number pulled out of thin air
            if (environmentMode == EnvironmentMode.FULL_ASSERT) {
                planningEntityTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return planningEntityTabuAcceptor;
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
        if (planningEntityTabuSize == null) {
            planningEntityTabuSize = inheritedConfig.getPlanningEntityTabuSize();
        }
        if (fadingPlanningEntityTabuSize == null) {
            fadingPlanningEntityTabuSize = inheritedConfig.getFadingPlanningEntityTabuSize();
        }
        if (planningValueTabuSize == null) {
            planningValueTabuSize = inheritedConfig.getPlanningValueTabuSize();
        }
        if (fadingPlanningValueTabuSize == null) {
            fadingPlanningValueTabuSize = inheritedConfig.getFadingPlanningValueTabuSize();
        }
        if (moveTabuSize == null) {
            moveTabuSize = inheritedConfig.getMoveTabuSize();
        }
        if (fadingMoveTabuSize == null) {
            fadingMoveTabuSize = inheritedConfig.getFadingMoveTabuSize();
        }
        if (undoMoveTabuSize == null) {
            undoMoveTabuSize = inheritedConfig.getUndoMoveTabuSize();
        }
        if (fadingUndoMoveTabuSize == null) {
            fadingUndoMoveTabuSize = inheritedConfig.getFadingUndoMoveTabuSize();
        }
        if (solutionTabuSize == null) {
            solutionTabuSize = inheritedConfig.getSolutionTabuSize();
        }
        if (fadingSolutionTabuSize == null) {
            fadingSolutionTabuSize = inheritedConfig.getFadingSolutionTabuSize();
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
