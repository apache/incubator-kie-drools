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

package org.optaplanner.core.impl.domain.solution;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;

/**
 * Only used by OptaPlanner Workbench 6 (but not 7).
 * This class will be removed in 8.0.
 * @param <S> the {@link Score} type used by this use case
 * @deprecated Use {@link PlanningSolution#autoDiscoverMemberType()} instead.
 */
@PlanningSolution
@Deprecated
public abstract class AbstractSolution<S extends Score> implements Serializable {

    protected S score;

    @PlanningScore
    public S getScore() {
        return score;
    }

    public void setScore(S score) {
        this.score = score;
    }

    /**
     * @return a list with every problem fact that is in a field of this class
     * (directly or indirectly as an element of a {@link Collection} or {@link Map} field)
     */
    @ProblemFactCollectionProperty
    protected List<Object> getProblemFactList() {
        List<Object> problemFactList = new ArrayList<>();
        addProblemFactsFromClass(problemFactList, getClass());
        return problemFactList;
    }

    /**
     * Adds to an existing to {@link List} to avoid copying the entire list with {@link List#addAll(Collection)}.
     * @param factList never null
     * @param instanceClass never null
     */
    private void addProblemFactsFromClass(List<Object> factList, Class<?> instanceClass) {
        if (instanceClass.equals(AbstractSolution.class)) {
            // The field score should not be included
            return;
        }
        for (Field field : instanceClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (isFieldAPlanningEntityPropertyOrPlanningEntityCollectionProperty(field, instanceClass)) {
                continue;
            }
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + instanceClass + ") has a field (" + field
                        + ") which can not be read to create the problem facts.", e);
            }
            if (value != null) {
                if (value instanceof Collection) {
                    factList.addAll((Collection) value);
                } else if (value instanceof Map) {
                    throw new IllegalStateException("The class (" + instanceClass + ") has a field (" + field
                            + ") which is a " + Map.class.getSimpleName() + " and that's not yet supported.");
                } else {
                    factList.add(value);
                }
            }
        }
        Class<?> superclass = instanceClass.getSuperclass();
        if (superclass != null) {
            addProblemFactsFromClass(factList, superclass);
        }
    }

    private boolean isFieldAPlanningEntityPropertyOrPlanningEntityCollectionProperty(Field field,
            Class<?> fieldInstanceClass) {
        if (field.isAnnotationPresent(PlanningEntityProperty.class)
                || field.isAnnotationPresent(PlanningEntityCollectionProperty.class)) {
            return true;
        }
        Method getterMethod = ReflectionHelper.getGetterMethod(fieldInstanceClass, field.getName());
        if (getterMethod != null &&
                (getterMethod.isAnnotationPresent(PlanningEntityProperty.class)
                        || getterMethod.isAnnotationPresent(PlanningEntityCollectionProperty.class))) {
            return true;
        }
        return false;
    }

}
