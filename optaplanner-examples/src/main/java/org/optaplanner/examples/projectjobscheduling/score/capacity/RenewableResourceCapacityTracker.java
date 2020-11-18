/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.projectjobscheduling.score.capacity;

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
