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

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;

public class ActionRetractNodeImpl extends BaseNodeImpl implements ActionRetractNode {

    private TypeNode boundNode;

    public ActionRetractNodeImpl() {
        //Errai marshalling
    }

    public ActionRetractNodeImpl( final TypeNode boundNode ) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionRetractNodeImpl)) return false;

        ActionRetractNodeImpl nodes = (ActionRetractNodeImpl) o;

        if (boundNode != null ? !boundNode.equals(nodes.boundNode) : nodes.boundNode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return boundNode != null ? boundNode.hashCode() : 0;
    }
}
