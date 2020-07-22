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

package org.optaplanner.core.impl.score.stream.drools.common.nodes;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConstraintModelChildNode
        extends AbstractConstraintModelNode implements ChildNode {

    private final List<ConstraintGraphNode> parentNodeList = new ArrayList<>(0);

    AbstractConstraintModelChildNode(ConstraintGraphNodeType type) {
        super(type);
    }

    public final void addParentNode(ConstraintGraphNode node) {
        ConstraintGraphNode nonNullNode = requireNonNull(node);
        if (parentNodeList.contains(nonNullNode)) {
            throw new IllegalStateException("Node (" + this + ") already has this parent node (" + nonNullNode + ").");
        }
        parentNodeList.add(nonNullNode);
    }

    @Override
    public final Class getFactType() {
        if (parentNodeList.isEmpty()) {
            throw new IllegalStateException("Impossible state: Child node (" + this + ") has no parents.");
        } else if (parentNodeList.size() == 1) {
            AbstractConstraintModelNode parentNode = (AbstractConstraintModelNode) parentNodeList.get(0);
            if (parentNode instanceof FromNode) {
                return ((FromNode) parentNode).getFactType();
            } else if (parentNode instanceof AbstractConstraintModelChildNode) {
                return ((AbstractConstraintModelChildNode) parentNode).getFactType();
            } else {
                throw new IllegalStateException("Impossible state: Parent node (" + parentNode +
                        ") without fact type accessor.");
            }
        } else { // Parent is a join node.
            return null;
        }
    }

    @Override
    public final List<ConstraintGraphNode> getParentNodes() {
        return Collections.unmodifiableList(parentNodeList);
    }

}
