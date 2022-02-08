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

import java.util.Map;

import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;

public class EqualsLookUpStrategy implements LookUpStrategy {

    @Override
    public void addWorkingObject(Map<Object, Object> idToWorkingObjectMap, Object workingObject) {
        Object oldAddedObject = idToWorkingObjectMap.put(workingObject, workingObject);
        if (oldAddedObject != null) {
            throw new IllegalStateException("The workingObjects (" + oldAddedObject + ", " + workingObject
                    + ") are equal (as in Object.equals()). Working objects must be unique.");
        }
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
    public <E> E lookUpWorkingObject(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        E workingObject = (E) idToWorkingObjectMap.get(externalObject);
        if (workingObject == null) {
            throw new IllegalStateException("The externalObject (" + externalObject
                    + ") has no known workingObject (" + workingObject + ").\n"
                    + "Maybe the workingObject was never added because the planning solution doesn't have a @"
                    + ProblemFactCollectionProperty.class.getSimpleName()
                    + " annotation on a member with instances of the externalObject's class ("
                    + externalObject.getClass() + ").");
        }
        return workingObject;
    }

    @Override
    public <E> E lookUpWorkingObjectIfExists(Map<Object, Object> idToWorkingObjectMap, E externalObject) {
        return (E) idToWorkingObjectMap.get(externalObject);
    }

}
