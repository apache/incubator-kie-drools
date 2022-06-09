package org.optaplanner.core.impl.domain.lookup;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * @see PlanningId
 * @see ScoreDirector#lookUpWorkingObject(Object)
 */
public class LookUpManager {

    private final LookUpStrategyResolver lookUpStrategyResolver;

    private Map<Object, Object> idToWorkingObjectMap;

    public LookUpManager(LookUpStrategyResolver lookUpStrategyResolver) {
        this.lookUpStrategyResolver = lookUpStrategyResolver;
        reset();
    }

    public void reset() {
        idToWorkingObjectMap = new HashMap<>();
    }

    public void addWorkingObject(Object workingObject) {
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(workingObject);
        lookUpStrategy.addWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void removeWorkingObject(Object workingObject) {
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(workingObject);
        lookUpStrategy.removeWorkingObject(idToWorkingObjectMap, workingObject);
    }

    /**
     * As defined by {@link ScoreDirector#lookUpWorkingObject(Object)}.
     *
     * @param externalObject sometimes null
     * @return null if externalObject is null
     * @throws IllegalArgumentException if there is no workingObject for externalObject, if it cannot be looked up
     *         or if the externalObject's class is not supported
     * @throws IllegalStateException if it cannot be looked up
     * @param <E> the object type
     */
    public <E> E lookUpWorkingObject(E externalObject) {
        if (externalObject == null) {
            return null;
        }
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(externalObject);
        return lookUpStrategy.lookUpWorkingObject(idToWorkingObjectMap, externalObject);
    }

    /**
     * As defined by {@link ScoreDirector#lookUpWorkingObjectOrReturnNull(Object)}.
     *
     * @param externalObject sometimes null
     * @return null if externalObject is null or if there is no workingObject for externalObject
     * @throws IllegalArgumentException if it cannot be looked up or if the externalObject's class is not supported
     * @throws IllegalStateException if it cannot be looked up
     * @param <E> the object type
     */
    public <E> E lookUpWorkingObjectOrReturnNull(E externalObject) {
        if (externalObject == null) {
            return null;
        }
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(externalObject);
        return lookUpStrategy.lookUpWorkingObjectIfExists(idToWorkingObjectMap, externalObject);
    }

}
