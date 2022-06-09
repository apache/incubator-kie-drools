package org.optaplanner.core.impl.domain.lookup;

import java.util.Map;

import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;

public class EqualsLookUpStrategy implements LookUpStrategy {

    @Override
    public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        Object oldAddedObject = idToWorkingObjectMap.put(workingObject, workingObject);
        if (oldAddedObject != null) {
            throw new IllegalStateException("The workingObjects (" + oldAddedObject + ", " + workingObject
                    + ") are equal (as in Object.equals()). Working objects must be unique.");
        }
    }

    @Override
    public void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        Object removedObject = idToWorkingObjectMap.remove(workingObject);
        if (workingObject != removedObject) {
            throw new IllegalStateException("The workingObject (" + workingObject
                    + ") differs from the removedObject (" + removedObject + ").");
        }
    }

    @Override
    public <E> E lookUpWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        E workingObject = (E) idToWorkingObjectMap.get(externalObject);
        if (workingObject == null) {
            throw new IllegalStateException("The externalObject (" + externalObject
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
        return (E) idToWorkingObjectMap.get(externalObject);
    }

}
