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

package org.drools.planner.examples.manners2009.solver.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.manners2009.domain.Manners2009;
import org.drools.planner.examples.manners2009.domain.SeatDesignation;
import org.drools.planner.examples.manners2009.solver.move.SeatDesignationSwitchMove;

public class SeatDesignationSwitchMoveFactory extends CachedMoveFactory {

    public List<Move> createCachedMoveList(Solution solution) {
        Manners2009 manners2009 = (Manners2009) solution;
        List<SeatDesignation> seatDesignationList = manners2009.getSeatDesignationList();
        List<Move> moveList = new ArrayList<Move>();
        for (ListIterator<SeatDesignation> leftIt = seatDesignationList.listIterator(); leftIt.hasNext();) {
            SeatDesignation leftSeatDesignation = leftIt.next();
            for (ListIterator<SeatDesignation> rightIt = seatDesignationList.listIterator(leftIt.nextIndex());
                    rightIt.hasNext();) {
                SeatDesignation rightSeatDesignation = rightIt.next();
                if (leftSeatDesignation.getGuest().getGender() == rightSeatDesignation.getGuest().getGender()) {
                    moveList.add(new SeatDesignationSwitchMove(leftSeatDesignation, rightSeatDesignation));
                }
            }
        }
        return moveList;
    }

}
