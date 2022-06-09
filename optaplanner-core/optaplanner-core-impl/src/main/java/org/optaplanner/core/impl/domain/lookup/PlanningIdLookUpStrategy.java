package org.optaplanner.core.impl.domain.lookup;

import java.util.Map;

import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.util.Pair;

public class PlanningIdLookUpStrategy implements LookUpStrategy {

    private final MemberAccessor planningIdMemberAccessor;

    public PlanningIdLookUpStrategy(MemberAccessor planningIdMemberAccessor) {
        this.planningIdMemberAccessor = planningIdMemberAccessor;
    }

    @Override
    public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        Object planningId = extractPlanningId(workingObject);
        if (planningId == null) {
            throw new IllegalStateException("The workingObject (" + workingObject
                    + ") returns a null value for its @" + PlanningId.class.getSimpleName()
                    + " member (" + planningIdMemberAccessor + ").");
        }
        Object oldAddedObject = idToWorkingObjectMap.put(planningId, workingObject);
        if (oldAddedObject != null) {
            throw new IllegalStateException("The workingObjects (" + oldAddedObject + ", " + workingObject
                    + ") have the same planningId (" + planningId + "). Working objects must be unique.");
        }
    }

    @Override
    public void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        Object planningId = extractPlanningId(workingObject);
        if (planningId == null) {
            throw new IllegalStateException("The workingObject (" + workingObject
                    + ") returns a null value for its @" + PlanningId.class.getSimpleName()
                    + " member (" + planningIdMemberAccessor + ").");
        }
        Object removedObject = idToWorkingObjectMap.remove(planningId);
        if (workingObject != removedObject) {
            throw new IllegalStateException("The workingObject (" + workingObject
                    + ") differs from the removedObject (" + removedObject + ") for planningId (" + planningId + ").");
        }
    }

    @Override
    public <E> E lookUpWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        Object planningId = extractPlanningId(externalObject);
        E workingObject = (E) idToWorkingObjectMap.get(planningId);
        if (workingObject == null) {
            throw new IllegalStateException("The externalObject (" + externalObject + ") with planningId (" + planningId
                    + ") has no known workingObject (" + workingObject + ").\n"
                    + "Maybe the workingObject was never added because the planning solution doesn't have a @"
                    + ProblemFactCollectionProperty.class.getSimpleName()
                    + " annotation on a member with instances of the externalObject's class ("
                    + externalObject.getClass() + ").");
        }
        return workingObject;
    }

    @Override
    public <E> E lookUpWorkingObjectIfExists(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        Object planningId = extractPlanningId(externalObject);
        return (E) idToWorkingObjectMap.get(planningId);
    }

    protected Object extractPlanningId(Object externalObject) {
        Object planningId = planningIdMemberAccessor.executeGetter(externalObject);
        if (planningId == null) {
            throw new IllegalArgumentException("The planningId (" + planningId
                    + ") of the member (" + planningIdMemberAccessor + ") of the class (" + externalObject.getClass()
                    + ") on externalObject (" + externalObject
                    + ") must not be null.\n"
                    + "Maybe initialize the planningId of the class (" + externalObject.getClass().getSimpleName()
                    + ") instance (" + externalObject + ") before solving.\n" +
                    "Maybe remove the @" + PlanningId.class.getSimpleName() + " annotation"
                    + " or change the @" + PlanningSolution.class.getSimpleName() + " annotation's "
                    + LookUpStrategyType.class.getSimpleName() + ".");
        }
        return Pair.of(externalObject.getClass(), planningId);
    }

}
