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

package org.drools.planner.examples.travelingtournament.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

public class Day extends AbstractPersistable implements Comparable<Day> {

    private int index;

    private Day nextDay;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Day getNextDay() {
        return nextDay;
    }

    public void setNextDay(Day nextDay) {
        this.nextDay = nextDay;
    }

    public int compareTo(Day other) {
        return new CompareToBuilder()
                .append(index, other.index)
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }

}
