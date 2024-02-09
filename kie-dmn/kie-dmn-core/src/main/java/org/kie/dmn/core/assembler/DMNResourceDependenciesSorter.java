/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DMNResourceDependenciesSorter {

    /**
     * Return a new list of DMNResource sorted by dependencies (required dependencies comes first)
     */
    public static List<DMNResource> sort(List<DMNResource> ins) {
        // In a graph A -> B -> {C, D}
        // showing that A requires B, and B requires C,D
        // then a depth-first visit would satisfy required ordering, for example a valid depth first visit is also a valid sort here: C, D, B, A.
        Collection<DMNResource> visited = new ArrayList<>(ins.size());
        List<DMNResource> dfv = new ArrayList<>(ins.size());

        for (DMNResource node : ins) {
            if (!visited.contains(node)) {
                dfVisit(node, ins, visited, dfv);
            }
        }

        return dfv;
    }

    /**
     * Performs a depth first visit, but keeping a separate reference of visited/visiting nodes, _also_ to avoid potential issues of circularities.
     */
    private static void dfVisit(DMNResource node, List<DMNResource> allNodes, Collection<DMNResource> visited, List<DMNResource> dfv) {
        if (visited.contains(node)) {
            throw new RuntimeException("Circular dependency detected: " + visited + " , and again to: " + node);
        }
        visited.add(node);

        List<DMNResource> neighbours = node.getDependencies().stream()
                                           .flatMap(dep -> allNodes.stream().filter(r -> r.getModelID().equals(dep)))
                                           .collect(Collectors.toList());
        for (DMNResource n : neighbours) {
            if (!visited.contains(n)) {
                dfVisit(n, allNodes, visited, dfv);
            }
        }

        dfv.add(node);
    }
}
