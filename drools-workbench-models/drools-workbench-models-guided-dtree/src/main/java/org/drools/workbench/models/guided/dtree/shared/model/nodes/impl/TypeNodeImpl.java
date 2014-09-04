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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;

public class TypeNodeImpl implements TypeNode {

    private String className;
    private List<Node> children = new ArrayList<Node>();

    public TypeNodeImpl() {
        //Errai marshalling
    }

    public TypeNodeImpl( final String className ) {
        setClassName( className );
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public void setClassName( final String className ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    @Override
    public List<Node> getChildren() {
        return this.children;
    }

}
