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

package org.drools.planner.examples.cloudbalancing.solver.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcessAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.solver.move.CloudProcessAssignmentSwitchMove;

public class CloudProcessAssignmentSwitchMoveFactory extends CachedMoveFactory {

    public List<Move> createCachedMoveList(Solution solution) {
        CloudBalance cloudBalance = (CloudBalance) solution;
        List<CloudProcessAssignment> cloudProcessAssignmentList = cloudBalance.getCloudProcessAssignmentList();
        List<Move> moveList = new ArrayList<Move>();
        for (ListIterator<CloudProcessAssignment> leftIt = cloudProcessAssignmentList.listIterator(); leftIt.hasNext();) {
            CloudProcessAssignment leftCloudProcessAssignment = leftIt.next();
            for (ListIterator<CloudProcessAssignment> rightIt = cloudProcessAssignmentList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                CloudProcessAssignment rightCloudProcessAssignment = rightIt.next();
                moveList.add(new CloudProcessAssignmentSwitchMove(leftCloudProcessAssignment, rightCloudProcessAssignment));
            }
        }
        return moveList;
    }

}
