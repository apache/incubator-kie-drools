/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.domain;

import java.util.List;

import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CsgBusHub")
public class BusHub extends AbstractPersistable implements StopOrHub {

    protected String name;
    protected RoadLocation location;

    // Shadow variables
    protected List<Shuttle> transferShuttleList;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public RoadLocation getLocation() {
        return location;
    }

    public void setLocation(RoadLocation location) {
        this.location = location;
    }

    @Override
    public List<Shuttle> getTransferShuttleList() {
        return transferShuttleList;
    }

    @Override
    public void setTransferShuttleList(List<Shuttle> transferShuttleList) {
        this.transferShuttleList = transferShuttleList;
    }

    @Override
    public Integer getTransportTimeToHub() {
        return 0;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public boolean isVisitedByCoach() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
