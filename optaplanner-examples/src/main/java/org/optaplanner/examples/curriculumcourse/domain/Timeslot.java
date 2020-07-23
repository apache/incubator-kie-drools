/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Timeslot")
public class Timeslot extends AbstractPersistable {

    private static final String[] TIMES = { "08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00" };

    private int timeslotIndex;

    public Timeslot(int timeslotIndex) {
        super(timeslotIndex);
        this.timeslotIndex = timeslotIndex;
    }

    public int getTimeslotIndex() {
        return timeslotIndex;
    }

    public void setTimeslotIndex(int timeslotIndex) {
        this.timeslotIndex = timeslotIndex;
    }

    public String getLabel() {
        String time = TIMES[timeslotIndex % TIMES.length];
        if (timeslotIndex > TIMES.length) {
            return "Timeslot " + timeslotIndex;
        }
        return time;
    }

    @Override
    public String toString() {
        return Integer.toString(timeslotIndex);
    }

}
