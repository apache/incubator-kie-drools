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
import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.domain.id.PlanningId;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * @see PlanningId
 * @see ScoreDirector#locateWorkingObject(Object)
 */
public class Locator {

    protected final Map<Class, LocationStrategy> decisionClassCache = new HashMap<>();

    public Locator() {
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

    public <E> E locateWorkingObject(E externalObject) {
        if (externalObject == null) {
            return null;
        }
        // TODO HACK UNEFFICIENT BUGGY IMPLEMENTATION!!!!

//        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
//        for (Object object : solutionDescriptor.getAllFacts(workingSolution)) {
//            if (externalObject.toString().equals(object.toString())) {
//                return (E) object;
//            }
//        }

        throw new IllegalArgumentException("The externalObject (" + externalObject + ") cannot be located.");
    }

    public void clearWorkingSolution() {

    }

    private interface LocationStrategy {

    }

    private static class ImmutableLocationStrategy implements LocationStrategy {

    }

    private static class PlanningIdLocationStrategy implements LocationStrategy {

    }

    private static class EqualsLocationStrategy implements LocationStrategy {

    }

}
