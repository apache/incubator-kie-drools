package org.optaplanner.core.impl.domain.lookup;

import java.util.Map;

import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

public class NoneLookUpStrategy implements LookUpStrategy {

    @Override
    public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        // Do nothing
    }

    @Override
    public void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        // Do nothing
    }

    @Override
    public <E> E lookUpWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        throw new IllegalArgumentException("The externalObject (" + externalObject
                + ") cannot be looked up. Some functionality, such as multithreaded solving, requires this ability.\n"
                + "Maybe add a @" + PlanningId.class.getSimpleName()
                + " annotation on an identifier property of the class (" + externalObject.getClass() + ").\n"
                + "Or otherwise, maybe change the @" + PlanningSolution.class.getSimpleName() + " annotation's "
                + LookUpStrategyType.class.getSimpleName() + " (not recommended).");
    }

    @Override
    public <E> E lookUpWorkingObjectIfExists(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        throw new IllegalArgumentException("The externalObject (" + externalObject
                + ") cannot be looked up. Some functionality, such as multithreaded solving, requires this ability.\n"
                + "Maybe add a @" + PlanningId.class.getSimpleName()
                + " annotation on an identifier property of the class (" + externalObject.getClass() + ").\n"
                + "Or otherwise, maybe change the @" + PlanningSolution.class.getSimpleName() + " annotation's "
                + LookUpStrategyType.class.getSimpleName() + " (not recommended).");
    }

}
