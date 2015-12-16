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

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;

public class TypeNodeImpl extends BaseBoundNodeImpl implements TypeNode {

    private String className;

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
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TypeNodeImpl ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        TypeNodeImpl nodes = (TypeNodeImpl) o;

        if ( !className.equals( nodes.className ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
}
