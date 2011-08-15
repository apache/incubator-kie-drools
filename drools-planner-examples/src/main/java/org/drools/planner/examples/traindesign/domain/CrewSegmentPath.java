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

package org.drools.planner.examples.traindesign.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

/**
 * A CrewSegment can be used multiple times on different trains.
 */
@XStreamAlias("CrewSegmentPath")
public class CrewSegmentPath extends AbstractPersistable implements Comparable<CrewSegmentPath> {

    private CrewSegment crewSegment;
    private List<RailArc> railPath;

    public CrewSegment getCrewSegment() {
        return crewSegment;
    }

    public void setCrewSegment(CrewSegment crewSegment) {
        this.crewSegment = crewSegment;
    }

    public List<RailArc> getRailPath() {
        return railPath;
    }

    public void setRailPath(List<RailArc> railPath) {
        this.railPath = railPath;
    }

    public int compareTo(CrewSegmentPath other) {
        return new CompareToBuilder()
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return crewSegment + "(" + railPath + ")";
    }

}
