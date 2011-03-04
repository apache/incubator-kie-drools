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

package org.drools.planner.examples.travelingtournament.domain.solver;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.examples.travelingtournament.domain.Team;

public class Hop implements Comparable<Hop>, Serializable {

    private Team team;
    private Team fromTeam;
    private Team toTeam;

    public Hop(Team team, Team fromTeam, Team toTeam) {
        this.team = team;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getFromTeam() {
        return fromTeam;
    }

    public void setFromTeam(Team fromTeam) {
        this.fromTeam = fromTeam;
    }

    public Team getToTeam() {
        return toTeam;
    }

    public void setToTeam(Team toTeam) {
        this.toTeam = toTeam;
    }

    public int getDistance() {
        Map<Team, Integer> distanceToTeamMap = fromTeam.getDistanceToTeamMap();
        return distanceToTeamMap.get(toTeam);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Hop) {
            Hop other = (Hop) o;
            return new EqualsBuilder()
                    .append(team, other.team)
                    .append(fromTeam, other.fromTeam)
                    .append(toTeam, other.toTeam)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(team)
                .append(fromTeam)
                .append(toTeam)
                .toHashCode();
    }

    public int compareTo(Hop other) {
        return new CompareToBuilder()
                .append(team, other.team)
                .append(fromTeam, other.fromTeam)
                .append(toTeam, other.toTeam)
                .toComparison();
    }

    @Override
    public String toString() {
        return team + ": " + fromTeam + " -> " + toTeam;
    }

}
