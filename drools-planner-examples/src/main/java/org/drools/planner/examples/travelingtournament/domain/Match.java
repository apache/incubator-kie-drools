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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

public class Match extends AbstractPersistable implements Comparable<Match> {

    private Team homeTeam;
    private Team awayTeam;

    private Day day;

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public int compareTo(Match other) {
        return new CompareToBuilder()
                .append(day, other.day)
                .append(homeTeam, other.homeTeam)
                .append(awayTeam, other.awayTeam)
                .append(id, other.id)
                .toComparison();
    }

    public Match clone() {
        Match clone = new Match();
        clone.id = id;
        clone.homeTeam = homeTeam;
        clone.awayTeam = awayTeam;
        clone.day = day;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used
     * because the rule engine already requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Match) {
            Match other = (Match) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(homeTeam, other.homeTeam)
                    .append(awayTeam, other.awayTeam)
                    .append(day, other.day)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used
     * because the rule engine already requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(homeTeam)
                .append(awayTeam)
                .append(day)
                .toHashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " " + homeTeam + " + " + awayTeam + " @ " + day;
    }

}
