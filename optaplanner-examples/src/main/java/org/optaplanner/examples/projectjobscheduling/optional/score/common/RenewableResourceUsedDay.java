package org.optaplanner.examples.projectjobscheduling.optional.score.common;

import java.util.Objects;

import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

public class RenewableResourceUsedDay {

    private final Resource resource;
    private final int usedDay;

    public RenewableResourceUsedDay(Resource resource, int usedDay) {
        this.resource = resource;
        this.usedDay = usedDay;
    }

    public Resource getResource() {
        return resource;
    }

    public int getUsedDay() {
        return usedDay;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RenewableResourceUsedDay other = (RenewableResourceUsedDay) o;
        return usedDay == other.usedDay &&
                Objects.equals(resource, other.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, usedDay);
    }

    @Override
    public String toString() {
        return resource + " on " + usedDay;
    }

    public int getResourceCapacity() {
        return resource.getCapacity();
    }

}
