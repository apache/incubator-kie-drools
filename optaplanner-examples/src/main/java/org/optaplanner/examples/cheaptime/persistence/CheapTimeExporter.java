/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.examples.cheaptime.app.CheapTimeApp;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerPrice;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.score.CheapTimeCostCalculator;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;

public class CheapTimeExporter extends AbstractTxtSolutionExporter<CheapTimeSolution> {

    public static void main(String[] args) {
        SolutionConverter<CheapTimeSolution> converter = SolutionConverter.createExportConverter(
                CheapTimeApp.DATA_DIR_NAME, CheapTimeSolution.class, new CheapTimeExporter());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return "txt";
    }

    @Override
    public TxtOutputBuilder<CheapTimeSolution> createTxtOutputBuilder() {
        return new CheapTimeOutputBuilder();
    }

    public static class CheapTimeOutputBuilder extends TxtOutputBuilder<CheapTimeSolution> {

        @Override
        public void writeSolution() throws IOException {
            int globalPeriodRangeTo = solution.getGlobalPeriodRangeTo();
            List<Machine> machineList = solution.getMachineList();
            List<PeriodPowerPrice> periodPowerPriceList = solution.getPeriodPowerPriceList();
            bufferedWriter.write(machineList.size() + "\n");
            Map<Machine, List<Boolean>> machinePeriodActiveListMap = createMachinePeriodActiveListMap(machineList);
            for (Machine machine : machineList) {
                bufferedWriter.write(machine.getId() + "\n");
                List<Boolean> periodActiveList = machinePeriodActiveListMap.get(machine);
                int spinCount = 0;
                StringBuilder spinUpDownLines = new StringBuilder("");
                MachinePeriodStatus previousStatus = MachinePeriodStatus.OFF;
                long idleCostMicros = 0L;
                int lastSpinDown = Integer.MIN_VALUE;
                for (int i = 0; i < globalPeriodRangeTo; i++) {
                    boolean active = periodActiveList.get(i);
                    if (active) {
                        if (previousStatus == MachinePeriodStatus.OFF) {
                            if (lastSpinDown >= 0) {
                                spinCount++;
                                spinUpDownLines.append("0 ").append(lastSpinDown).append("\n");
                                lastSpinDown = Integer.MIN_VALUE;
                            }
                            // Spin up
                            spinCount++;
                            spinUpDownLines.append("1 ").append(i).append("\n");
                        }
                        previousStatus = MachinePeriodStatus.ACTIVE;
                    } else {
                        if (previousStatus != MachinePeriodStatus.OFF) {
                            if (previousStatus == MachinePeriodStatus.ACTIVE) {
                                lastSpinDown = i - 1;
                            }
                            PeriodPowerPrice periodPowerPrice = periodPowerPriceList.get(i);
                            idleCostMicros += CheapTimeCostCalculator.multiplyTwoMicros(
                                    machine.getPowerConsumptionMicros(), periodPowerPrice.getPowerPriceMicros());
                            if (idleCostMicros > machine.getSpinUpDownCostMicros()) {
                                idleCostMicros = 0L;
                                previousStatus = MachinePeriodStatus.OFF;
                            } else {
                                previousStatus = MachinePeriodStatus.IDLE;
                            }
                        }
                    }
                }
                if (previousStatus == MachinePeriodStatus.ACTIVE) {
                    lastSpinDown = globalPeriodRangeTo - 1;
                }
                if (lastSpinDown >= 0) {
                    spinCount++;
                    spinUpDownLines.append("0 ").append(lastSpinDown).append("\n");
                    lastSpinDown = Integer.MIN_VALUE;
                }
                bufferedWriter.write(spinCount + "\n");
                bufferedWriter.write(spinUpDownLines.toString());
            }
            List<TaskAssignment> taskAssignmentList = solution.getTaskAssignmentList();
            bufferedWriter.write(taskAssignmentList.size() + "\n");
            for (TaskAssignment taskAssignment : taskAssignmentList) {
                bufferedWriter.write(taskAssignment.getTask().getId() + " "
                        + taskAssignment.getMachine().getId() + " "
                        + taskAssignment.getStartPeriod() + "\n");
            }
        }

        private Map<Machine, List<Boolean>> createMachinePeriodActiveListMap(List<Machine> machineList) {
            Map<Machine, List<Boolean>> machinePeriodActiveListMap = new LinkedHashMap<>(machineList.size());
            if (solution.getGlobalPeriodRangeFrom() != 0) {
                throw new IllegalStateException("The globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                        + ") should be 0.");
            }
            for (Machine machine : machineList) {
                ArrayList<Boolean> periodActiveList = new ArrayList<>(solution.getGlobalPeriodRangeTo());
                for (int i = 0; i < solution.getGlobalPeriodRangeTo(); i++) {
                    periodActiveList.add(false);
                }
                machinePeriodActiveListMap.put(machine, periodActiveList);
            }
            for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
                for (int i = taskAssignment.getStartPeriod(); i < taskAssignment.getEndPeriod(); i++) {
                    machinePeriodActiveListMap.get(taskAssignment.getMachine()).set(i, true);
                }
            }
            return machinePeriodActiveListMap;
        }

        private enum MachinePeriodStatus {
            OFF,
            IDLE,
            ACTIVE;
        }

    }

}
