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

import java.util.Objects;

import org.optaplanner.core.impl.score.stream.drools.common.ConstraintGraph;

public final class FromNode<A> extends AbstractConstraintModelNode implements UniConstraintGraphNode {

    private final Class<A> factType;
    private final ConstraintGraph graph;

    public FromNode(Class<A> factType, ConstraintGraph graph) {
        super(ConstraintGraphNodeType.FROM);
        this.factType = Objects.requireNonNull(factType);
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public Class<A> getFactType() {
        return factType;
    }

    public ConstraintGraph getGraph() {
        return graph;
    }
}
