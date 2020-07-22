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
import java.util.Objects;

import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;

public abstract class AbstractConstraintModelNode implements ConstraintGraphNode {

    private final List<ConstraintGraphNode> childNodeList = new ArrayList<>(0);
    private final List<ConstraintConsequence> consequenceList = new ArrayList<>(0);
    private final ConstraintGraphNodeType type;

    AbstractConstraintModelNode(ConstraintGraphNodeType type) {
        this.type = Objects.requireNonNull(type);
    }

    public final void addChildNode(ConstraintGraphNode node) {
        ConstraintGraphNode nonNullNode = requireNonNull(node);
        if (childNodeList.contains(nonNullNode)) {
            throw new IllegalStateException("Node (" + this + ") already has this child node (" + nonNullNode + ").");
        }
        childNodeList.add(nonNullNode);
    }

    public final void addConsequence(ConstraintConsequence consequence) {
        ConstraintConsequence nonNullConsequence = requireNonNull(consequence);
        if (consequenceList.contains(nonNullConsequence)) {
            throw new IllegalStateException("Node (" + this + ") already has this consequence (" + nonNullConsequence + ").");
        }
        consequenceList.add(nonNullConsequence);
    }

    @Override
    public final List<ConstraintGraphNode> getChildNodes() {
        return Collections.unmodifiableList(childNodeList);
    }

    @Override
    public final List<ConstraintConsequence> getConsequences() {
        return Collections.unmodifiableList(consequenceList);
    }

    @Override
    public final ConstraintGraphNodeType getType() {
        return type;
    }
}
