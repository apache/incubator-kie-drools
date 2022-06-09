package org.optaplanner.core.impl.domain.lookup;

import java.util.Map;

public class ImmutableLookUpStrategy implements LookUpStrategy {

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
        // Because it is immutable, we can use the same one.
        return externalObject;
    }

    @Override
    public <E> E lookUpWorkingObjectIfExists(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        // Because it is immutable, we can use the same one.
        return externalObject;
    }

}
