package org.optaplanner.examples.projectscheduling.solver.score.capacity;

import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;

public class RenewableResourceCapacityTracker extends ResourceCapacityTracker {

    public RenewableResourceCapacityTracker(Resource resource) {
        super(resource);
    }

    @Override
    public void insert(ResourceRequirement resourceRequirement, Allocation allocation) {
        // TODO generated
    }

    @Override
    public void retract(ResourceRequirement resourceRequirement, Allocation allocation) {
        // TODO generated
    }

    @Override
    public int getHardScore() {
        return 0;
    }

}
