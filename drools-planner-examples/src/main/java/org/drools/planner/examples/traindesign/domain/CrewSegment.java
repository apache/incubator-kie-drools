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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CrewSegment")
public class CrewSegment extends AbstractPersistable implements Comparable<CrewSegment> {

    private RailNode origin;
    private RailNode destination;

    public RailNode getOrigin() {
        return origin;
    }

    public void setOrigin(RailNode origin) {
        this.origin = origin;
    }

    public RailNode getDestination() {
        return destination;
    }

    public void setDestination(RailNode destination) {
        this.destination = destination;
    }

    public int compareTo(CrewSegment other) {
        return new CompareToBuilder()
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return origin + "->" + destination;
    }

}
