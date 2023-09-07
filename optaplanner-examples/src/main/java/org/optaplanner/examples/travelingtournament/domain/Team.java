/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.travelingtournament.domain;

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.persistence.jackson.KeySerializer;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Team extends AbstractPersistable implements Labeled {

    private String name;
    private Map<Team, Integer> distanceToTeamMap;

    public Team() {
    }

    public Team(long id) {
        super(id);
    }

    public Team(long id, String name) {
        this(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = TeamKeyDeserializer.class)
    public Map<Team, Integer> getDistanceToTeamMap() {
        return distanceToTeamMap;
    }

    public void setDistanceToTeamMap(Map<Team, Integer> distanceToTeamMap) {
        this.distanceToTeamMap = distanceToTeamMap;
    }

    @JsonIgnore
    public int getDistance(Team other) {
        return distanceToTeamMap.get(other);
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
