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

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.id.LocationStrategyType;
import org.optaplanner.core.api.domain.id.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

class PlanningIdLocationStrategy implements LocationStrategy {

    private MemberAccessor memberAccessor;

    public PlanningIdLocationStrategy(MemberAccessor memberAccessor) {
        this.memberAccessor = memberAccessor;
    }

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
        return (E) idToWorkingObjectMap.get(key);
    }

    protected Object extractKey(Object externalObject) {
        Object planningId = memberAccessor.executeGetter(externalObject);
        if (planningId == null) {
            throw new IllegalStateException("The planningId (" + planningId
                    + ") of the member (" + memberAccessor + ") on externalObject (" + externalObject
                    + ") must not be null.\n"
                    + "Maybe initialize the planningId of the original object before solving" +
                    " or remove the " + PlanningId.class.getSimpleName() + " annotation"
                    + " or change the " + PlanningSolution.class.getSimpleName() + " annotation's "
                    + LocationStrategyType.class.getSimpleName() + ".");
        }
        return Pair.of(externalObject.getClass(), planningId);
    }

}
