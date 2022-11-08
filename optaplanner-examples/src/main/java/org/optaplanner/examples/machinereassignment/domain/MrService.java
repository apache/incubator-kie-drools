package org.optaplanner.examples.machinereassignment.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = MrService.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MrService extends AbstractPersistableJackson {

    private List<MrService> toDependencyServiceList;
    private List<MrService> fromDependencyServiceList;

    private int locationSpread;

    @SuppressWarnings("unused")
    MrService() { // For Jackson.
    }

    public MrService(long id) {
        super(id);
    }

    public List<MrService> getToDependencyServiceList() {
        return toDependencyServiceList;
    }

    public void setToDependencyServiceList(List<MrService> toDependencyServiceList) {
        this.toDependencyServiceList = toDependencyServiceList;
    }

    public List<MrService> getFromDependencyServiceList() {
        return fromDependencyServiceList;
    }

    public void setFromDependencyServiceList(List<MrService> fromDependencyServiceList) {
        this.fromDependencyServiceList = fromDependencyServiceList;
    }

    public int getLocationSpread() {
        return locationSpread;
    }

    public void setLocationSpread(int locationSpread) {
        this.locationSpread = locationSpread;
    }

}
