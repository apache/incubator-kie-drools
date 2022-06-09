package org.optaplanner.examples.cloudbalancing.optional.score;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudBalancingMapBasedEasyScoreCalculator implements EasyScoreCalculator<CloudBalance, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(CloudBalance cloudBalance) {
        int computerListSize = cloudBalance.getComputerList().size();
        Map<CloudComputer, Integer> cpuPowerUsageMap = new HashMap<>(computerListSize);
        Map<CloudComputer, Integer> memoryUsageMap = new HashMap<>(computerListSize);
        Map<CloudComputer, Integer> networkBandwidthUsageMap = new HashMap<>(computerListSize);
        for (CloudComputer computer : cloudBalance.getComputerList()) {
            cpuPowerUsageMap.put(computer, 0);
            memoryUsageMap.put(computer, 0);
            networkBandwidthUsageMap.put(computer, 0);
        }
        Set<CloudComputer> usedComputerSet = new HashSet<>(computerListSize);

        visitProcessList(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap,
                usedComputerSet, cloudBalance.getProcessList());

        int hardScore = sumHardScore(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap);
        int softScore = sumSoftScore(usedComputerSet);

        return HardSoftScore.of(hardScore, softScore);
    }

    private void visitProcessList(Map<CloudComputer, Integer> cpuPowerUsageMap,
            Map<CloudComputer, Integer> memoryUsageMap, Map<CloudComputer, Integer> networkBandwidthUsageMap,
            Set<CloudComputer> usedComputerSet, List<CloudProcess> processList) {
        // We loop through the processList only once for performance
        for (CloudProcess process : processList) {
            CloudComputer computer = process.getComputer();
            if (computer != null) {
                int cpuPowerUsage = cpuPowerUsageMap.get(computer) + process.getRequiredCpuPower();
                cpuPowerUsageMap.put(computer, cpuPowerUsage);
                int memoryUsage = memoryUsageMap.get(computer) + process.getRequiredMemory();
                memoryUsageMap.put(computer, memoryUsage);
                int networkBandwidthUsage = networkBandwidthUsageMap.get(computer) + process.getRequiredNetworkBandwidth();
                networkBandwidthUsageMap.put(computer, networkBandwidthUsage);
                usedComputerSet.add(computer);
            }
        }
    }

    private int sumHardScore(Map<CloudComputer, Integer> cpuPowerUsageMap, Map<CloudComputer, Integer> memoryUsageMap,
            Map<CloudComputer, Integer> networkBandwidthUsageMap) {
        int hardScore = 0;
        for (Map.Entry<CloudComputer, Integer> usageEntry : cpuPowerUsageMap.entrySet()) {
            CloudComputer computer = usageEntry.getKey();
            int cpuPowerAvailable = computer.getCpuPower() - usageEntry.getValue();
            if (cpuPowerAvailable < 0) {
                hardScore += cpuPowerAvailable;
            }
        }
        for (Map.Entry<CloudComputer, Integer> usageEntry : memoryUsageMap.entrySet()) {
            CloudComputer computer = usageEntry.getKey();
            int memoryAvailable = computer.getMemory() - usageEntry.getValue();
            if (memoryAvailable < 0) {
                hardScore += memoryAvailable;
            }
        }
        for (Map.Entry<CloudComputer, Integer> usageEntry : networkBandwidthUsageMap.entrySet()) {
            CloudComputer computer = usageEntry.getKey();
            int networkBandwidthAvailable = computer.getNetworkBandwidth() - usageEntry.getValue();
            if (networkBandwidthAvailable < 0) {
                hardScore += networkBandwidthAvailable;
            }
        }
        return hardScore;
    }

    private int sumSoftScore(Set<CloudComputer> usedComputerSet) {
        int softScore = 0;
        for (CloudComputer usedComputer : usedComputerSet) {
            softScore -= usedComputer.getCost();
        }
        return softScore;
    }

}
