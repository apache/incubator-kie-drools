package org.optaplanner.examples.travelingtournament.domain;

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TtpTeam")
public class Team extends AbstractPersistable {

    private String name;
    private Map<Team, Integer> distanceToTeamMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Team, Integer> getDistanceToTeamMap() {
        return distanceToTeamMap;
    }

    public void setDistanceToTeamMap(Map<Team, Integer> distanceToTeamMap) {
        this.distanceToTeamMap = distanceToTeamMap;
    }

    public int getDistance(Team other) {
        return distanceToTeamMap.get(other);
    }

    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
