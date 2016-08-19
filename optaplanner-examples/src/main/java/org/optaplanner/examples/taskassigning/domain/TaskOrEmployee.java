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

package org.optaplanner.examples.taskassigning.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public abstract class TaskOrEmployee extends AbstractPersistable {

    // Shadow variables
    @InverseRelationShadowVariable(sourceVariableName = "previousTaskOrEmployee")
    protected Task nextTask;

    public TaskOrEmployee() {
    }

    public TaskOrEmployee(long id) {
        super(id);
    }

    public Task getNextTask() {
        return nextTask;
    }

    public void setNextTask(Task nextTask) {
        this.nextTask = nextTask;
    }

    /**
     * @return sometimes null
     */
    public abstract Integer getEndTime();

    /**
     * @return sometimes null
     */
    public abstract Employee getEmployee();

}
