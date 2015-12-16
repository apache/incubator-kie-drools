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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;

public abstract class BaseNodeImpl implements Node {

    private Node parent;
    private List<Node> children = new ArrayList<Node>();

    public BaseNodeImpl() {
        //Errai marshalling
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent( final Node node ) {
        parent = node;
    }

    @Override
    public void addChild( final Node node ) {
        node.setParent( this );
        children.add( node );
    }

    @Override
    public void removeChild( final Node node ) {
        node.setParent( null );
        children.remove( node );
        children.iterator();
    }

    @Override
    public List<Node> getChildren() {
        return Collections.unmodifiableList( children );
    }

    @Override
    public Iterator<Node> iterator() {
        return children.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseNodeImpl)) return false;

        BaseNodeImpl nodes = (BaseNodeImpl) o;

        if (children != null ? !children.equals(nodes.children) : nodes.children != null) return false;
        if (parent != null ? !parent.equals(nodes.parent) : nodes.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
