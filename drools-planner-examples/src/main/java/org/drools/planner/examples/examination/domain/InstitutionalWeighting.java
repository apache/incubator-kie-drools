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

package org.drools.planner.examples.examination.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("InstitutionalWeighting")
public class InstitutionalWeighting extends AbstractPersistable implements Comparable<InstitutionalWeighting> {

    private int twoInARowPenality;
    private int twoInADayPenality;
    private int periodSpreadLength;
    private int periodSpreadPenality;
    private int mixedDurationPenality;
    private int frontLoadLargeTopicSize;
    private int frontLoadLastPeriodSize;
    private int frontLoadPenality;

    public int getTwoInARowPenality() {
        return twoInARowPenality;
    }

    public void setTwoInARowPenality(int twoInARowPenality) {
        this.twoInARowPenality = twoInARowPenality;
    }

    public int getTwoInADayPenality() {
        return twoInADayPenality;
    }

    public void setTwoInADayPenality(int twoInADayPenality) {
        this.twoInADayPenality = twoInADayPenality;
    }

    public int getPeriodSpreadLength() {
        return periodSpreadLength;
    }

    public void setPeriodSpreadLength(int periodSpreadLength) {
        this.periodSpreadLength = periodSpreadLength;
    }

    public int getPeriodSpreadPenality() {
        return periodSpreadPenality;
    }

    public void setPeriodSpreadPenality(int periodSpreadPenality) {
        this.periodSpreadPenality = periodSpreadPenality;
    }

    public int getMixedDurationPenality() {
        return mixedDurationPenality;
    }

    public void setMixedDurationPenality(int mixedDurationPenality) {
        this.mixedDurationPenality = mixedDurationPenality;
    }

    public int getFrontLoadLargeTopicSize() {
        return frontLoadLargeTopicSize;
    }

    public void setFrontLoadLargeTopicSize(int frontLoadLargeTopicSize) {
        this.frontLoadLargeTopicSize = frontLoadLargeTopicSize;
    }

    public int getFrontLoadLastPeriodSize() {
        return frontLoadLastPeriodSize;
    }

    public void setFrontLoadLastPeriodSize(int frontLoadLastPeriodSize) {
        this.frontLoadLastPeriodSize = frontLoadLastPeriodSize;
    }

    public int getFrontLoadPenality() {
        return frontLoadPenality;
    }

    public void setFrontLoadPenality(int frontLoadPenality) {
        this.frontLoadPenality = frontLoadPenality;
    }

    public int compareTo(InstitutionalWeighting other) {
        return new CompareToBuilder()
                .append(id, other.id)
                .toComparison();
    }

}
