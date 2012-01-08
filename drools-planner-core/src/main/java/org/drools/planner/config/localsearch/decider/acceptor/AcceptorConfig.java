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
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.acceptor.CompositeAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.PropertyTabuAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.SolutionTabuAcceptor;
import org.drools.planner.core.score.definition.ScoreDefinition;

@XStreamAlias("acceptor")
public class AcceptorConfig {

    private Acceptor acceptor = null; // TODO make into a list
    private Class<? extends Acceptor> acceptorClass = null;

    @XStreamImplicit(itemFieldName = "acceptorType")
    private List<AcceptorType> acceptorTypeList = null;

    protected Integer moveTabuSize = null;
    protected Integer partialMoveTabuSize = null;
    protected Integer undoMoveTabuSize = null;
    protected Integer partialUndoMoveTabuSize = null;
    protected Integer propertyTabuSize = null;
    protected Integer partialPropertyTabuSize = null;
    protected Integer solutionTabuSize = null;
    protected Integer partialSolutionTabuSize = null;

    protected String simulatedAnnealingStartingTemperature = null;

    protected Double greatDelugeWaterLevelUpperBoundRate = null;
    protected Double greatDelugeWaterRisingRate = null;

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public Class<? extends Acceptor> getAcceptorClass() {
        return acceptorClass;
    }

    public void setAcceptorClass(Class<? extends Acceptor> acceptorClass) {
        this.acceptorClass = acceptorClass;
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

    public Integer getPropertyTabuSize() {
        return propertyTabuSize;
    }

    public void setPropertyTabuSize(Integer propertyTabuSize) {
        this.propertyTabuSize = propertyTabuSize;
    }

    public Integer getPartialPropertyTabuSize() {
        return partialPropertyTabuSize;
    }

    public void setPartialPropertyTabuSize(Integer partialPropertyTabuSize) {
        this.partialPropertyTabuSize = partialPropertyTabuSize;
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

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Acceptor buildAcceptor(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition) {
        List<Acceptor> acceptorList = new ArrayList<Acceptor>();
        if (acceptor != null) {
            acceptorList.add(acceptor);
        }
        if (acceptorClass != null) {
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
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.PROPERTY_TABU))
                || propertyTabuSize != null || partialPropertyTabuSize != null) {
            PropertyTabuAcceptor propertyTabuAcceptor = new PropertyTabuAcceptor();
            if (propertyTabuSize != null) {
                propertyTabuAcceptor.setTabuSize(propertyTabuSize);
            }
            if (partialPropertyTabuSize != null) {
                propertyTabuAcceptor.setPartialTabuSize(partialPropertyTabuSize);
            }
            if (environmentMode == EnvironmentMode.TRACE) {
                propertyTabuAcceptor.setAssertTabuHashCodeCorrectness(true);
            }
            acceptorList.add(propertyTabuAcceptor);
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
        if ((acceptorTypeList != null && acceptorTypeList.contains(AcceptorType.LATE_ACCEPTANCE))) {
            // TODO implement LATE_ACCEPTANCE
            throw new UnsupportedOperationException("LATE_ACCEPTANCE not yet supported.");
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
        // inherited acceptors get compositely added
        if (acceptor == null) {
            acceptor = inheritedConfig.getAcceptor();
        }
        if (acceptorClass == null) {
            acceptorClass = inheritedConfig.getAcceptorClass();
        }
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
        if (propertyTabuSize == null) {
            propertyTabuSize = inheritedConfig.getPropertyTabuSize();
        }
        if (partialPropertyTabuSize == null) {
            partialPropertyTabuSize = inheritedConfig.getPartialPropertyTabuSize();
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
    }

    public static enum AcceptorType {
        MOVE_TABU,
        UNDO_MOVE_TABU,
        PROPERTY_TABU,
        SOLUTION_TABU,
        SIMULATED_ANNEALING,
        LATE_ACCEPTANCE,
        GREAT_DELUGE,
    }

}
