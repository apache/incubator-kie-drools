package org.optaplanner.examples.projectscheduling.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.ExecutionMode;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;
import org.optaplanner.examples.projectscheduling.domain.ResourceRequirement;
import org.optaplanner.examples.projectscheduling.domain.resource.GlobalResource;
import org.optaplanner.examples.projectscheduling.domain.resource.LocalResource;
import org.optaplanner.examples.projectscheduling.domain.resource.Resource;
import org.optaplanner.examples.projectscheduling.solver.score.capacity.NonrenewableResourceCapacityTracker;
import org.optaplanner.examples.projectscheduling.solver.score.capacity.RenewableResourceCapacityTracker;
import org.optaplanner.examples.projectscheduling.solver.score.capacity.ResourceCapacityTracker;

public class ProjectSchedulingIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<ProjectsSchedule> {

    private Map<Resource, ResourceCapacityTracker> resourceCapacityTrackerMap;

    private int hardScore;
    private int mediumScore;
    private int softScore;

    public void resetWorkingSolution(ProjectsSchedule projectsSchedule) {
        List<GlobalResource> globalResourceList = projectsSchedule.getGlobalResourceList();
        List<LocalResource> localResourceList = projectsSchedule.getLocalResourceList();
        resourceCapacityTrackerMap = new HashMap<Resource, ResourceCapacityTracker>(
                globalResourceList.size() + localResourceList.size());
        for (Resource resource : globalResourceList) {
            resourceCapacityTrackerMap.put(resource, resource.isRenewable()
                    ? new RenewableResourceCapacityTracker(resource)
                    : new NonrenewableResourceCapacityTracker(resource));
        }
        for (Resource resource : localResourceList) {
            resourceCapacityTrackerMap.put(resource, resource.isRenewable()
                    ? new RenewableResourceCapacityTracker(resource)
                    : new NonrenewableResourceCapacityTracker(resource));
        }
        hardScore = 0;
        mediumScore = 0;
        softScore = 0;
        for (Allocation allocation : projectsSchedule.getAllocationList()) {
            insert(allocation);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert((Allocation) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Allocation) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Allocation) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Allocation) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Allocation allocation) {
        ExecutionMode executionMode = allocation.getExecutionMode();
        if (executionMode != null) {
            for (ResourceRequirement resourceRequirement : executionMode.getResourceRequirementList()) {
                ResourceCapacityTracker tracker = resourceCapacityTrackerMap.get(
                        resourceRequirement.getResource());
                hardScore -= tracker.getHardScore();
                tracker.insert(resourceRequirement, allocation);
                hardScore += tracker.getHardScore();
            }
        }
    }

    private void retract(Allocation allocation) {
        ExecutionMode executionMode = allocation.getExecutionMode();
        if (executionMode != null) {
            for (ResourceRequirement resourceRequirement : executionMode.getResourceRequirementList()) {
                ResourceCapacityTracker tracker = resourceCapacityTrackerMap.get(
                        resourceRequirement.getResource());
                hardScore -= tracker.getHardScore();
                tracker.retract(resourceRequirement, allocation);
                hardScore += tracker.getHardScore();
            }
        }
    }

    public Score calculateScore() {
        return BendableScore.valueOf(new int[] {hardScore}, new int[] {mediumScore, softScore});
    }

}
