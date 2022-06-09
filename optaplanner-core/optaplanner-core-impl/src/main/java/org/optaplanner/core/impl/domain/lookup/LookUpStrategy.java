package org.optaplanner.core.impl.domain.lookup;

import java.util.Map;

public interface LookUpStrategy {

    void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject);

    void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject);

    <E> E lookUpWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject);

    <E> E lookUpWorkingObjectIfExists(Map<Object, Object> idToWorkingObjectMap, E externalObject);

}
