package org.optaplanner.examples.projectjobscheduling.optional.score.common;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

public class RenewableResourceCapacityTracker extends ResourceCapacityTracker {

    protected int capacityEveryDay;

    protected Map<Integer, Integer> usedPerDay;
    protected int hardScore;

    public RenewableResourceCapacityTracker(Resource resource) {
        super(resource);
        if (!resource.isRenewable()) {
            throw new IllegalArgumentException("The resource (" + resource + ") is expected to be renewable.");
        }
        capacityEveryDay = resource.getCapacity();
        usedPerDay = new HashMap<>();
        hardScore = 0;
    }

    @Override
    public void insert(ResourceRequirement resourceRequirement, Allocation allocation) {
        int startDate = allocation.getStartDate();
        int endDate = allocation.getEndDate();
        int requirement = resourceRequirement.getRequirement();
        for (int i = startDate; i < endDate; i++) {
            Integer used = usedPerDay.get(i);
            if (used == null) {
                used = 0;
            }
            if (used > capacityEveryDay) {
                hardScore += (used - capacityEveryDay);
            }
            used += requirement;
            if (used > capacityEveryDay) {
                hardScore -= (used - capacityEveryDay);
            }
            usedPerDay.put(i, used);
        }
    }

    @Override
    public void retract(ResourceRequirement resourceRequirement, Allocation allocation) {
        int startDate = allocation.getStartDate();
        int endDate = allocation.getEndDate();
        int requirement = resourceRequirement.getRequirement();
        for (int i = startDate; i < endDate; i++) {
            Integer used = usedPerDay.get(i);
            if (used == null) {
                used = 0;
            }
            if (used > capacityEveryDay) {
                hardScore += (used - capacityEveryDay);
            }
            used -= requirement;
            if (used > capacityEveryDay) {
                hardScore -= (used - capacityEveryDay);
            }
            usedPerDay.put(i, used);
        }
    }

    @Override
    public int getHardScore() {
        return hardScore;
    }

}
