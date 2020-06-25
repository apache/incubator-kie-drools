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

package org.optaplanner.core.impl.domain.lookup;

import java.util.Collection;
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
    }

    public void resetWorkingObjects(Collection<Object> allFacts) {
        idToWorkingObjectMap = new HashMap<>(allFacts.size());
        for (Object fact : allFacts) {
            addWorkingObject(fact);
        }
    }

    public void addWorkingObject(Object workingObject) {
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(workingObject);
        lookUpStrategy.addWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void removeWorkingObject(Object workingObject) {
        LookUpStrategy lookUpStrategy = lookUpStrategyResolver.determineLookUpStrategy(workingObject);
        lookUpStrategy.removeWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void clearWorkingObjects() {
        idToWorkingObjectMap = null;
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
