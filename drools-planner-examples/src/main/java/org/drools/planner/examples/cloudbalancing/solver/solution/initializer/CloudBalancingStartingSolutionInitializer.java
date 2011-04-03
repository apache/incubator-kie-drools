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

package org.drools.planner.examples.cloudbalancing.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.initializer.AbstractStartingSolutionInitializer;
import org.drools.planner.core.solver.AbstractSolverScope;
import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;
import org.drools.planner.examples.common.domain.PersistableIdComparator;

public class CloudBalancingStartingSolutionInitializer extends AbstractStartingSolutionInitializer {

    @Override
    public boolean isSolutionInitialized(AbstractSolverScope abstractSolverScope) {
        CloudBalance cloudBalance = (CloudBalance) abstractSolverScope.getWorkingSolution();
        return cloudBalance.isInitialized();
    }

    public void initializeSolution(AbstractSolverScope abstractSolverScope) {
        CloudBalance cloudBalance = (CloudBalance) abstractSolverScope.getWorkingSolution();
        initializeCloudAssignmentList(abstractSolverScope, cloudBalance);
    }

    private void initializeCloudAssignmentList(AbstractSolverScope abstractSolverScope,
            CloudBalance cloudBalance) {
        List<CloudComputer> cloudComputerList = cloudBalance.getCloudComputerList();
        WorkingMemory workingMemory = abstractSolverScope.getWorkingMemory();

        List<CloudAssignment> cloudAssignmentList = createCloudAssignmentList(cloudBalance);
        for (CloudAssignment cloudAssignment : cloudAssignmentList) {
            FactHandle cloudAssignmentHandle = null;
            Score bestScore = DefaultHardAndSoftScore.valueOf(Integer.MIN_VALUE, Integer.MIN_VALUE);
            CloudComputer bestCloudComputer = null;
            for (CloudComputer cloudComputer : cloudComputerList) {
                cloudAssignment.setCloudComputer(cloudComputer);
                if (cloudAssignmentHandle == null) {
                    cloudAssignmentHandle = workingMemory.insert(cloudAssignment);
                } else {
                    workingMemory.update(cloudAssignmentHandle, cloudAssignment);
                }
                Score score = abstractSolverScope.calculateScoreFromWorkingMemory();
                if (score.compareTo(bestScore) > 0) {
                    bestScore = score;
                    bestCloudComputer = cloudComputer;
                }
            }
            if (bestCloudComputer == null) {
                throw new IllegalStateException("The bestCloudComputer (" + bestCloudComputer + ") cannot be null.");
            }
            cloudAssignment.setCloudComputer(bestCloudComputer);
            workingMemory.update(cloudAssignmentHandle, cloudAssignment);
            logger.debug("    CloudAssignment ({}) initialized for starting solution.", cloudAssignment);
        }

        Collections.sort(cloudAssignmentList, new PersistableIdComparator());
        cloudBalance.setCloudAssignmentList(cloudAssignmentList);
    }

    public List<CloudAssignment> createCloudAssignmentList(CloudBalance cloudBalance) {
        List<CloudProcess> cloudProcessList = cloudBalance.getCloudProcessList();

        List<CloudProcessInitializationWeight> cloudProcessInitializationWeightList
                = new ArrayList<CloudProcessInitializationWeight>(cloudProcessList.size());
        for (CloudProcess cloudProcess : cloudProcessList) {
            cloudProcessInitializationWeightList.add(new CloudProcessInitializationWeight(cloudBalance, cloudProcess));
        }
        Collections.sort(cloudProcessInitializationWeightList);

        List<CloudAssignment> cloudAssignmentList = new ArrayList<CloudAssignment>(cloudProcessList.size());
        int cloudAssignmentId = 0;
        for (CloudProcessInitializationWeight cloudProcessInitializationWeight : cloudProcessInitializationWeightList) {
            CloudProcess cloudProcess = cloudProcessInitializationWeight.getCloudProcess();
            CloudAssignment cloudAssignment = new CloudAssignment();
            cloudAssignment.setId((long) cloudAssignmentId);
            cloudAssignment.setCloudProcess(cloudProcess);
            cloudAssignmentList.add(cloudAssignment);
            cloudAssignmentId++;
        }
        return cloudAssignmentList;
    }

    private class CloudProcessInitializationWeight implements Comparable<CloudProcessInitializationWeight> {

        private CloudProcess cloudProcess;

        private CloudProcessInitializationWeight(CloudBalance cloudBalance, CloudProcess cloudProcess) {
            this.cloudProcess = cloudProcess;
        }

        public CloudProcess getCloudProcess() {
            return cloudProcess;
        }

        public int compareTo(CloudProcessInitializationWeight other) {
            return new CompareToBuilder()
                    .append(other.cloudProcess.getMinimalMultiplicand(), cloudProcess.getMinimalMultiplicand()) // Descending
                    .toComparison();
        }

    }

}
