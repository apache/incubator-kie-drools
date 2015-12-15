/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.dinnerparty.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

@PlanningEntity
@XStreamAlias("SeatDesignation")
public class SeatDesignation extends AbstractPersistable implements Labeled {

    private Guest guest;
    private Seat seat;

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @PlanningVariable(valueRangeProviderRefs = {"seatRange"})
    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getGuestName() {
        return getGuest().getName();
    }

    public Gender getGuestGender() {
        return getGuest().getGender();
    }

    public Job getGuestJob() {
        return getGuest().getJob();
    }

    public JobType getGuestJobType() {
        return getGuest().getJob().getJobType();
    }

    public boolean differentKindIfNeeded(Job otherGuestJob) {
        JobType jobType = guest.getJob().getJobType();
        return jobType == JobType.SOCIALITE || jobType == JobType.TEACHER || guest.getJob() != otherGuestJob;
    }

    public Table getSeatTable() {
        if (seat == null) {
            return  null;
        }
        return seat.getTable();
    }

    public boolean isRightOf(SeatDesignation leftSeatDesignation) {
        if (seat == null || leftSeatDesignation.seat == null) {
            return false;
        }
        return seat.getRightSeat() == leftSeatDesignation.seat;
    }

    public boolean isNeighborOf(SeatDesignation otherSeatDesignation) {
        if (seat == null || otherSeatDesignation.seat == null) {
            return false;
        }
        return seat.getLeftSeat() == otherSeatDesignation.seat || seat.getRightSeat() == otherSeatDesignation.seat;
    }

    public String getLabel() {
        return guest.getLabel();
    }

    @Override
    public String toString() {
        return guest.toString();
    }

}
