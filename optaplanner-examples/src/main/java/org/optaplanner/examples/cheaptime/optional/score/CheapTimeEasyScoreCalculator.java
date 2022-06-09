package org.optaplanner.examples.cheaptime.optional.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Period;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.score.CheapTimeCostCalculator;

public class CheapTimeEasyScoreCalculator implements EasyScoreCalculator<CheapTimeSolution, HardMediumSoftLongScore> {

    @Override
    public HardMediumSoftLongScore calculateScore(CheapTimeSolution solution) {
        if (solution.getGlobalPeriodRangeFrom() != 0) {
            throw new IllegalStateException("The globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                    + ") should be 0.");
        }
        int globalPeriodRangeTo = solution.getGlobalPeriodRangeTo();
        List<Machine> machineList = solution.getMachineList();
        Map<Machine, List<MachinePeriodPart>> machinePeriodListMap = new LinkedHashMap<>(machineList.size());
        for (Machine machine : machineList) {
            List<MachinePeriodPart> machinePeriodList = new ArrayList<>(globalPeriodRangeTo);
            for (int period = 0; period < globalPeriodRangeTo; period++) {
                machinePeriodList.add(new MachinePeriodPart(machine, period, Collections.emptyList()));
            }
            machinePeriodListMap.put(machine, machinePeriodList);
        }
        long mediumScore = 0L;
        long softScore = 0L;
        List<Period> periodList = solution.getPeriodList();
        for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
            Machine machine = taskAssignment.getMachine();
            Integer startPeriod = taskAssignment.getStartPeriod();
            if (machine != null && startPeriod != null) {
                List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(machine);
                int endPeriod = taskAssignment.getEndPeriod();
                for (int period = startPeriod; period < endPeriod; period++) {
                    MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
                    machinePeriodPart.addTaskAssignment(taskAssignment);
                    Period powerPrice = periodList.get(period);
                    mediumScore -= CheapTimeCostCalculator.multiplyTwoMicros(
                            taskAssignment.getTask().getPowerConsumptionMicros(),
                            powerPrice.getPowerPriceMicros());
                }
                softScore -= startPeriod;
            }
        }
        long hardScore = 0L;
        for (Map.Entry<Machine, List<MachinePeriodPart>> entry : machinePeriodListMap.entrySet()) {
            Machine machine = entry.getKey();
            List<MachinePeriodPart> machinePeriodList = entry.getValue();
            MachinePeriodStatus previousStatus = MachinePeriodStatus.OFF;
            long idleCostMicros = 0L;
            for (Period period : solution.getPeriodList()) {
                MachinePeriodPart machinePeriodPart = machinePeriodList.get(period.getIndex());
                boolean active = machinePeriodPart.isActive();
                if (active) {
                    if (previousStatus == MachinePeriodStatus.OFF) {
                        // Spin up
                        mediumScore -= machine.getSpinUpDownCostMicros();
                    } else if (previousStatus == MachinePeriodStatus.IDLE) {
                        // Pay idle cost
                        mediumScore -= idleCostMicros;
                        idleCostMicros = 0L;
                    }
                    hardScore += machinePeriodPart.getResourceInShortTotal();
                    mediumScore -= CheapTimeCostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                            period.getPowerPriceMicros());
                    previousStatus = MachinePeriodStatus.ACTIVE;
                } else {
                    if (previousStatus != MachinePeriodStatus.OFF) {
                        idleCostMicros += CheapTimeCostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                                period.getPowerPriceMicros());
                        if (idleCostMicros > machine.getSpinUpDownCostMicros()) {
                            idleCostMicros = 0L;
                            previousStatus = MachinePeriodStatus.OFF;
                        } else {
                            previousStatus = MachinePeriodStatus.IDLE;
                        }
                    }
                }
            }
        }
        return HardMediumSoftLongScore.of(hardScore, mediumScore, softScore);
    }

    private enum MachinePeriodStatus {
        OFF,
        IDLE,
        ACTIVE;
    }

}
