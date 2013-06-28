package org.optaplanner.examples.projectscheduling.solver.score.capacity;

import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;

public abstract class ResourceCapacityTracker {

    protected Resource resource;

    public ResourceCapacityTracker(Resource resource) {
        this.resource = resource;
    }

    public abstract void insert(ResourceRequirement resourceRequirement, Allocation allocation);

    public abstract void retract(ResourceRequirement resourceRequirement, Allocation allocation);

    public abstract int getHardScore();

}
