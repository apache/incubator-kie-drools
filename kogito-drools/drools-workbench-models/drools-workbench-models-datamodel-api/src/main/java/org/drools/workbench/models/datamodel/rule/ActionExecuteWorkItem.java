/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;

/**
 * An Action to invoke a Work Item
 */
public class ActionExecuteWorkItem
        implements
        IAction {

    private PortableWorkDefinition workDefinition;

    public ActionExecuteWorkItem() {

    }

    public PortableWorkDefinition getWorkDefinition() {
        return workDefinition;
    }

    public void setWorkDefinition( PortableWorkDefinition workDefinition ) {
        this.workDefinition = workDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionExecuteWorkItem that = (ActionExecuteWorkItem) o;

        if (workDefinition != null ? !workDefinition.equals(that.workDefinition) : that.workDefinition != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return workDefinition != null ? workDefinition.hashCode() : 0;
    }
}
