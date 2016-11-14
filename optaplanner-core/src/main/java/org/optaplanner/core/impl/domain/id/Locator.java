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

package org.optaplanner.core.impl.domain.id;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.domain.id.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see PlanningId
 * @see ScoreDirector#locateWorkingObject(Object)
 */
public class Locator<Solution_> {

    // TODO move into SolutionDescriptor so the decision is cached at a higher level? Causes Threading issues (shared with UI ScoreDirector)
    protected final Map<Class, LocationStrategy> decisionClassCache = new HashMap<>();
    protected final InnerScoreDirector<Solution_> scoreDirector;

    protected Map<Object, Object> idToWorkingObjectMap;

    public Locator(InnerScoreDirector<Solution_> scoreDirector) {
        this.scoreDirector = scoreDirector;
        decisionClassCache.put(Boolean.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Byte.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Short.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Integer.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Long.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Float.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Double.class, new ImmutableLocationStrategy());
        decisionClassCache.put(BigInteger.class, new ImmutableLocationStrategy());
        decisionClassCache.put(BigDecimal.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Character.class, new ImmutableLocationStrategy());
        decisionClassCache.put(String.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalDate.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalTime.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalDateTime.class, new ImmutableLocationStrategy());
    }

    public void resetWorkingObjects(Collection<Object> allFacts) {
        idToWorkingObjectMap = new HashMap<>(allFacts.size());
        for (Object fact : allFacts) {
            addWorkingObject(fact);
        }
    }

    public void addWorkingObject(Object workingObject) {
        LocationStrategy locationStrategy = retrieveLocationStrategy(workingObject);
        locationStrategy.addWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void removeWorkingObject(Object workingObject) {
        LocationStrategy locationStrategy = retrieveLocationStrategy(workingObject);
        locationStrategy.removeWorkingObject(idToWorkingObjectMap, workingObject);
    }

    public void clearWorkingObjects() {
        idToWorkingObjectMap = null;
    }

    public <E> E locateWorkingObject(E externalObject) {
        if (externalObject == null) {
            return null;
        }
        LocationStrategy locationStrategy = retrieveLocationStrategy(externalObject);
        return locationStrategy.locateWorkingObject(idToWorkingObjectMap, externalObject);
    }

    protected LocationStrategy retrieveLocationStrategy(Object object) {
        Class<?> objectClass = object.getClass();
        LocationStrategy locationStrategy = decisionClassCache.get(objectClass);
        if (locationStrategy == null) {
            locationStrategy = new EqualsLocationStrategy(); // TODO FIXME
            decisionClassCache.put(objectClass, locationStrategy);
        }
        return locationStrategy;
    }

    protected interface LocationStrategy {

        void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject);
        void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject);
        <E> E locateWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject);
    }

    protected static class ImmutableLocationStrategy implements LocationStrategy {

        @Override
        public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
            // Do nothing
        }

        @Override
        public void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
            // Do nothing
        }

        @Override
        public <E> E locateWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
            // Because it is immutable, we can use the same one.
            return externalObject;
        }

    }

    protected static class PlanningIdLocationStrategy implements LocationStrategy {

        @Override
        public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
            Object key = extractKey(workingObject);
            if (key == null) {
                throw new IllegalArgumentException("The workingObject (" + workingObject
                        + ") cannot be added because there is no key (" + key
                        + ") for the class (" + workingObject.getClass() + ").");
            }
            idToWorkingObjectMap.put(key, workingObject);
        }

        @Override
        public void removeWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
            Object key = extractKey(workingObject);
            Object removedObject = idToWorkingObjectMap.remove(key);
            if (workingObject != removedObject) {
                throw new IllegalStateException("The workingObject (" + workingObject
                        + ") differs from the removedObject (" + removedObject + ") for key (" + key + ").");
            }
        }

        @Override
        public <E> E locateWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
            Object key = extractKey(externalObject);
            if (key == null) {
                throw new IllegalArgumentException("The externalObject (" + externalObject
                        + ") cannot be located because there is no key (" + key
                        + ") for the class (" + externalObject.getClass() + ").\n"
                        + "Maybe add a " + PlanningId.class.getSimpleName() + " annotation or enable equals semantics.");
            }
            return (E) idToWorkingObjectMap.get(externalObject);
        }

        protected Object extractKey(Object externalObject) {
            return null; // TODO FIXME
        }

    }

    protected static class EqualsLocationStrategy implements LocationStrategy {

        @Override
        public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
            idToWorkingObjectMap.put(workingObject, workingObject);
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
        public <E> E locateWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
            return (E) idToWorkingObjectMap.get(externalObject);
        }

    }

}
