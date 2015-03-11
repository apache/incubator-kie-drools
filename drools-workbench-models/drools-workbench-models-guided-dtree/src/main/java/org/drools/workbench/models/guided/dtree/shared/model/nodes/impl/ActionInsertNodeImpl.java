/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.models.guided.dtree.shared.model.nodes.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;

public class ActionInsertNodeImpl extends BaseNodeImpl implements ActionInsertNode {

    private String className;
    private boolean isLogicalInsertion = false;
    private List<ActionFieldValue> fieldValues = new ArrayList<ActionFieldValue>();

    public ActionInsertNodeImpl() {
        //Errai marshalling
    }

    public ActionInsertNodeImpl( final String className ) {
        setClassName( className );
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName( final String className ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    @Override
    public boolean isLogicalInsertion() {
        return isLogicalInsertion;
    }

    @Override
    public void setLogicalInsertion( final boolean isLogicalInsertion ) {
        this.isLogicalInsertion = isLogicalInsertion;
    }

    @Override
    public List<ActionFieldValue> getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionInsertNodeImpl)) return false;

        ActionInsertNodeImpl nodes = (ActionInsertNodeImpl) o;

        if (isLogicalInsertion != nodes.isLogicalInsertion) return false;
        if (className != null ? !className.equals(nodes.className) : nodes.className != null) return false;
        if (fieldValues != null ? !fieldValues.equals(nodes.fieldValues) : nodes.fieldValues != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (isLogicalInsertion ? 1 : 0);
        result = ~~result;
        result = 31 * result + (fieldValues != null ? fieldValues.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
