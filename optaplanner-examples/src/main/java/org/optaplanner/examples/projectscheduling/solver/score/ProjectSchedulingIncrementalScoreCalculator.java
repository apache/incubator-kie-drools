package org.optaplanner.examples.projectscheduling.solver.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;

public class ProjectSchedulingIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<ProjectsSchedule> {

    public void resetWorkingSolution(ProjectsSchedule projectsSchedule) {
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

    private void insert(final Allocation entity) {
//        if (!entity.isInitialized()) {
//            return;
//        }
//        this.capacityTracker.add(entity);
//        this.precedenceRelationsTracker.add(entity);
//        this.projectPropertiesTracker.add(entity);
    }

    private void retract(final Allocation entity) {
//        if (!entity.isInitialized()) {
//            return;
//        }
//        this.capacityTracker.remove(entity);
//        this.precedenceRelationsTracker.remove(entity);
//        this.projectPropertiesTracker.remove(entity);
    }

    public Score calculateScore() {
//        final int brokenReq1and2and3Count = this.capacityTracker.getOverusedCapacity();
//        final int brokenReq7Count = this.precedenceRelationsTracker.getBrokenPrecedenceRelationsMeasure();
//        // now assemble the constraints
//        final int hard = brokenReq1and2and3Count + brokenReq7Count;
//        final int medium = this.projectPropertiesTracker.getTotalProjectDelay();
//        final int soft = this.projectPropertiesTracker.getTotalMakespan();
        return BendableScore.valueOf(new int[] {0}, new int[] {0, 0});
    }

}
