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
