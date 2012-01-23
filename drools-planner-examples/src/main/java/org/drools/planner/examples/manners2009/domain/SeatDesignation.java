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

package org.drools.planner.examples.manners2009.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@PlanningEntity
@XStreamAlias("SeatDesignation")
public class SeatDesignation extends AbstractPersistable {

    private Guest guest;
    private Seat seat;

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "seatList")
    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Job getGuestJob() {
        return getGuest().getJob();
    }

    public JobType getGuestJobType() {
        return getGuest().getJob().getJobType();
    }

    public Table getSeatTable() {
        if (seat == null) {
            return  null;
        }
        return seat.getTable();
    }

    public SeatDesignation clone() {
        SeatDesignation clone = new SeatDesignation();
        clone.id = id;
        clone.guest = guest;
        clone.seat = seat;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SeatDesignation) {
            SeatDesignation other = (SeatDesignation) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(guest, other.guest)
                    .append(seat, other.seat)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(guest)
                .append(seat)
                .toHashCode();
    }

    @Override
    public String toString() {
        return guest + " @ " + seat;
    }

}
