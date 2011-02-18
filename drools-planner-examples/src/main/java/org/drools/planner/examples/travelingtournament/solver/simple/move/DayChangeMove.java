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

package org.drools.planner.examples.travelingtournament.solver.simple.move;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.FactHandle;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.travelingtournament.domain.Day;
import org.drools.planner.examples.travelingtournament.domain.Match;

public class DayChangeMove implements Move, TabuPropertyEnabled {

    private Match match;
    private Day toDay;

    public DayChangeMove(Match match, Day toDay) {
        this.match = match;
        this.toDay = toDay;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(match.getDay(), toDay);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new DayChangeMove(match, match.getDay());
    }

    public void doMove(WorkingMemory workingMemory) {
        FactHandle matchHandle = workingMemory.getFactHandle(match);
        match.setDay(toDay);
        workingMemory.update(matchHandle, match);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(match);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof DayChangeMove) {
            DayChangeMove other = (DayChangeMove) o;
            return new EqualsBuilder()
                    .append(match, other.match)
                    .append(toDay, other.toDay)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(match)
                .append(toDay)
                .toHashCode();
    }

    public String toString() {
        return match + " => " + toDay;
    }

}
