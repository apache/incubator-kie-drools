package org.optaplanner.examples.projectscheduling.solver.score.capacity;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;

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
        usedPerDay = new HashMap<Integer, Integer>();
        hardScore = 0;
    }

    @Override
    public void insert(ResourceRequirement resourceRequirement, Allocation allocation) {
        Integer startDate = allocation.getStartDate();
        Integer endDate = allocation.getEndDate();
        if (startDate != null && endDate != null) {
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
    }

    @Override
    public void retract(ResourceRequirement resourceRequirement, Allocation allocation) {
        Integer startDate = allocation.getStartDate();
        Integer endDate = allocation.getEndDate();
        if (startDate != null && endDate != null) {
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
    }

    @Override
    public int getHardScore() {
        return hardScore;
    }

}
