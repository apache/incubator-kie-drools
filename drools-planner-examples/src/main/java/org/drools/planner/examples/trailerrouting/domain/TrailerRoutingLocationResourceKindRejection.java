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

package org.drools.planner.examples.trailerrouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TrailerRoutingRoute")
public class TrailerRoutingLocationResourceKindRejection extends AbstractPersistable implements Comparable<TrailerRoutingLocationResourceKindRejection> {

    private TrailerRoutingLocation location;
    private TrailerRoutingResourceKind resourceKind;

    public TrailerRoutingLocation getLocation() {
        return location;
    }

    public void setLocation(TrailerRoutingLocation location) {
        this.location = location;
    }

    public TrailerRoutingResourceKind getResourceKind() {
        return resourceKind;
    }

    public void setResourceKind(TrailerRoutingResourceKind resourceKind) {
        this.resourceKind = resourceKind;
    }

    public int compareTo(TrailerRoutingLocationResourceKindRejection other) {
        return new CompareToBuilder()
                .append(location, other.location)
                .append(resourceKind, other.resourceKind)
                .toComparison();
    }

    @Override
    public String toString() {
        return location + "-" + resourceKind;
    }

}
