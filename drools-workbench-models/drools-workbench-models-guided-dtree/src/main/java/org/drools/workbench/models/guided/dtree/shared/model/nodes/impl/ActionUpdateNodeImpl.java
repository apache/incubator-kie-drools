/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;

public class ActionUpdateNodeImpl extends BaseNodeImpl implements ActionUpdateNode {

    private TypeNode boundNode;
    private boolean isModify = false;
    private List<ActionFieldValue> fieldValues = new ArrayList<ActionFieldValue>();

    public ActionUpdateNodeImpl() {
        //Errai marshalling
    }

    public ActionUpdateNodeImpl( final TypeNode boundNode ) {
        setBoundNode( boundNode );
    }

    @Override
    public TypeNode getBoundNode() {
        return boundNode;
    }

    @Override
    public void setBoundNode( final TypeNode boundNode ) {
        this.boundNode = PortablePreconditions.checkNotNull( "boundNode",
                                                             boundNode );
    }

    @Override
    public boolean isModify() {
        return isModify;
    }

    @Override
    public void setModify( final boolean isModify ) {
        this.isModify = isModify;
    }

    @Override
    public List<ActionFieldValue> getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ActionUpdateNodeImpl ) ) {
            return false;
        }

        ActionUpdateNodeImpl nodes = (ActionUpdateNodeImpl) o;

        if ( isModify != nodes.isModify ) {
            return false;
        }
        if ( !boundNode.equals( nodes.boundNode ) ) {
            return false;
        }
        if ( !fieldValues.equals( nodes.fieldValues ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = boundNode.hashCode();
        result = 31 * result + ( isModify ? 1 : 0 );
        result = 31 * result + fieldValues.hashCode();
        return result;
    }
}
