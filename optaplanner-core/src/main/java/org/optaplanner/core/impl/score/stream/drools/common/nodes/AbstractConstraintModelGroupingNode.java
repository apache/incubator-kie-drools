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

import java.util.Collections;
import java.util.List;

public abstract class AbstractConstraintModelGroupingNode<FunctionType_, CollectorType_>
        extends AbstractConstraintModelChildNode {

    private final List<FunctionType_> mappings;
    private final List<CollectorType_> collectors;

    AbstractConstraintModelGroupingNode(List<FunctionType_> mappings, List<CollectorType_> collectors) {
        super(determineType(mappings, collectors));
        this.mappings = mappings.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(mappings);
        this.collectors = collectors.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(collectors);
    }

    private static ConstraintGraphNodeType determineType(List mappings, List collectors) {
        if (mappings.isEmpty()) {
            if (collectors.isEmpty()) {
                throw new IllegalStateException("Impossible state: Grouping node has no mappings or collectors.");
            } else {
                return ConstraintGraphNodeType.GROUPBY_COLLECTING_ONLY;
            }
        } else if (collectors.isEmpty()) {
            return ConstraintGraphNodeType.GROUPBY_MAPPING_ONLY;
        } else {
            return ConstraintGraphNodeType.GROUPBY_MAPPING_AND_COLLECTING;
        }
    }

    public List<FunctionType_> getMappings() {
        return mappings;
    }

    public List<CollectorType_> getCollectors() {
        return collectors;
    }
}
