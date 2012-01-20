/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.solver.move;

//import java.util.Collection;
//import java.util.Collections;
//
//import org.apache.commons.lang.ObjectUtils;
//import org.apache.commons.lang.builder.EqualsBuilder;
//import org.apache.commons.lang.builder.HashCodeBuilder;
//import org.drools.WorkingMemory;
//import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
//import org.drools.planner.core.move.Move;
//import org.drools.planner.examples.tsp.domain.Journey;

//public class SubTourChangeMove implements Move, TabuPropertyEnabled {
//
//    private Journey startJourney;
//    private Journey endJourney;
//
//    private Journey toAfterJourney;
//
//    public SubTourChangeMove(Journey startJourney, Journey endJourney, Journey toAfterJourney) {
//        this.startJourney = startJourney;
//        this.endJourney = endJourney;
//        this.toAfterJourney = toAfterJourney;
//    }
//
//    public boolean isMoveDoable(WorkingMemory workingMemory) {
//        Journey nextJourney = startJourney;
//        if (ObjectUtils.equals(startJourney, toAfterJourney.getNextJourney())) {
//            return false;
//        }
//        while (!ObjectUtils.equals(nextJourney, endJourney)) {
//            if (ObjectUtils.equals(nextJourney, toAfterJourney)) {
//                return false;
//            }
//            nextJourney = nextJourney.getNextJourney();
//        }
//        if (ObjectUtils.equals(endJourney, toAfterJourney)) {
//            return false;
//        }
//        return true;
//    }
//
//    public Move createUndoMove(WorkingMemory workingMemory) {
//        return new SubTourChangeMove(startJourney, endJourney,
//                startJourney.getPreviousTerminal());
//    }
//
//    public void doMove(WorkingMemory workingMemory) {
//        Journey newPreviousJourney = toAfterJourney;
//        Journey newNextJourney = newPreviousJourney.getNextJourney();
//        Journey originalPreviousJourney = startJourney.getPreviousTerminal();
//        Journey originalNextJourney = endJourney.getNextJourney();
//        TspMoveHelper.moveJourneyAfterJourney(workingMemory, newPreviousJourney, startJourney);
//        TspMoveHelper.moveJourneyAfterJourney(workingMemory, endJourney, newNextJourney);
//        TspMoveHelper.moveJourneyAfterJourney(workingMemory, originalPreviousJourney, originalNextJourney);
//    }
//
//    public Collection<? extends Object> getTabuProperties() {
//        return Collections.singletonList(startJourney.getCity());
//    }
//
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        } else if (o instanceof SubTourChangeMove) {
//            SubTourChangeMove other = (SubTourChangeMove) o;
//            return new EqualsBuilder()
//                    .append(startJourney, other.startJourney)
//                    .append(endJourney, other.endJourney)
//                    .append(toAfterJourney, other.toAfterJourney)
//                    .isEquals();
//        } else {
//            return false;
//        }
//    }
//
//    public int hashCode() {
//        return new HashCodeBuilder()
//                .append(startJourney)
//                .append(endJourney)
//                .append(toAfterJourney)
//                .toHashCode();
//    }
//
//    public String toString() {
//        return startJourney + "-" + endJourney + " => after " + toAfterJourney;
//    }
//
//}
