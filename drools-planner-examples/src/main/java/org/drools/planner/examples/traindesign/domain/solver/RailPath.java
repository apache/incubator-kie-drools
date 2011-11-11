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

package org.drools.planner.examples.traindesign.domain.solver;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.traindesign.domain.CrewSegment;
import org.drools.planner.examples.traindesign.domain.RailArc;

public class RailPath extends AbstractPersistable {

    private List<RailArc> railArcList;
    private int distance; // in miles * 1000 (to avoid Double rounding errors and BigDecimal)

    public RailPath(List<RailArc> railArcList) {
        this.railArcList = railArcList;
        distance = 0;
        for (RailArc railArc : railArcList) {
            distance += railArc.getDistance();
        }
    }

    public List<RailArc> getRailArcList() {
        return railArcList;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return railArcList.toString();
    }

}
