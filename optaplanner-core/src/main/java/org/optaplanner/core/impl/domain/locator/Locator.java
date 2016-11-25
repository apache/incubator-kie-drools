/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.locator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.domain.locator.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see PlanningId
 * @see ScoreDirector#locateWorkingObject(Object)
 */
public class Locator<Solution_> {

    private final LocationStrategyResolver locationStrategyResolver;

    private Map<Object, Object> idToWorkingObjectMap;

    public Locator(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.locationStrategyResolver = solutionDescriptor.getLocationStrategyResolver();
    }

    public void resetWorkingObjects(Collection<Object> allFacts) {
        idToWorkingObjectMap = new HashMap<>(allFacts.size());
        for (Object fact : allFacts) {
            addWorkingObject(fact);
        }
    }

    public void addWorkingObject(Object workingObject) {
        LocationStrategy locationStrategy = locationStrategyResolver.determineLocationStrategy(workingObject);
        locationStrategy.addWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void removeWorkingObject(Object workingObject) {
        LocationStrategy locationStrategy = locationStrategyResolver.determineLocationStrategy(workingObject);
        locationStrategy.removeWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void clearWorkingObjects() {
        idToWorkingObjectMap = null;
    }

    /**
     * As defined by {@link ScoreDirector#locateWorkingObject(Object)}.
     * @param externalObject sometimes null
     * @return only null if originEntity is null
     * @throws IllegalArgumentException if it cannot be located
     * @param <E> the object type
     */
    public <E> E locateWorkingObject(E externalObject) {
        if (externalObject == null) {
            return null;
        }
        LocationStrategy locationStrategy = locationStrategyResolver.determineLocationStrategy(externalObject);
        return locationStrategy.locateWorkingObject(idToWorkingObjectMap, externalObject);
    }

}
