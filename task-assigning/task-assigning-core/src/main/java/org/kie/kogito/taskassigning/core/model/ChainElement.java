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

package org.kie.kogito.taskassigning.core.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;

@PlanningEntity
public abstract class ChainElement extends IdentifiableElement {

    /**
     * Shadow variable for being able to move forward in the chain. So finally by using the nextElement and the
     * previousElement a double linked structure is created.
     *
     * <p>
     * User (the anchor) <-> A <-> B <-> C <-> D -> null
     * <p>
     * <p>
     * In this way given a ChainElement in a solution it's possible to iterate back and forward through the data structure.
     */
    @InverseRelationShadowVariable(sourceVariableName = PREVIOUS_ELEMENT)
    protected TaskAssignment nextElement;

    protected ChainElement() {
    }

    protected ChainElement(String id) {
        super(id);
    }

    public TaskAssignment getNextElement() {
        return nextElement;
    }

    public void setNextElement(TaskAssignment nextElement) {
        this.nextElement = nextElement;
    }

    public abstract boolean isTaskAssignment();
}
