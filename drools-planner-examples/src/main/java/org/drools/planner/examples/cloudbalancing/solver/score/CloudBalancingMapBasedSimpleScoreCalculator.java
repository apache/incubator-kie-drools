/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.cloudbalancing.solver.score;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.planner.core.score.buildin.hardandsoft.DefaultHardAndSoftScore;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.score.director.simple.SimpleScoreCalculator;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;

public class CloudBalancingMapBasedSimpleScoreCalculator implements SimpleScoreCalculator<CloudBalance> {

    public HardAndSoftScore calculateScore(CloudBalance cloudBalance) {
        int computerListSize = cloudBalance.getComputerList().size();
        Map<CloudComputer, Integer> cpuPowerUsageMap = new HashMap<CloudComputer, Integer>(computerListSize);
        Map<CloudComputer, Integer> memoryUsageMap = new HashMap<CloudComputer, Integer>(computerListSize);
        Map<CloudComputer, Integer> networkBandwidthUsageMap = new HashMap<CloudComputer, Integer>(computerListSize);
        for (CloudComputer computer : cloudBalance.getComputerList()) {
            cpuPowerUsageMap.put(computer, 0);
            memoryUsageMap.put(computer, 0);
            networkBandwidthUsageMap.put(computer, 0);
        }
        Set<CloudComputer> usedComputerSet = new HashSet<CloudComputer>(computerListSize);

        visitProcessList(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap,
                usedComputerSet, cloudBalance.getProcessList());

        int hardScore = sumHardScore(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap);
        int softScore = sumSoftScore(usedComputerSet);

        return DefaultHardAndSoftScore.valueOf(hardScore, softScore);
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
