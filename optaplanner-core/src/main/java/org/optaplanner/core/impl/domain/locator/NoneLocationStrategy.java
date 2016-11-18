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

import java.util.Map;

import org.optaplanner.core.api.domain.locator.LocationStrategyType;
import org.optaplanner.core.api.domain.locator.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class NoneLocationStrategy implements LocationStrategy {

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
        throw new IllegalArgumentException("The externalObject (" + externalObject
                + ") cannot be located.\n"
                + "Maybe give the class (" + externalObject.getClass()
                + ") a " + PlanningId.class.getSimpleName() + " annotation"
                + " or change the " + PlanningSolution.class.getSimpleName() + " annotation's "
                + LocationStrategyType.class.getSimpleName()
                + " or don't rely on functionality that depends on "
                + ScoreDirector.class.getSimpleName() + ".locateWorkingObject().");
    }

}
