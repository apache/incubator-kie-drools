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
