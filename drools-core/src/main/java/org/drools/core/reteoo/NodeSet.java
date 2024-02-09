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
package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.core.common.BaseNode;

public class NodeSet implements Iterable<BaseNode> {
    private final List<BaseNode> nodes = new ArrayList<>();
    private final Set<Integer> nodeIds = new HashSet<>();

    public List<BaseNode> getNodes() {
        return nodes;
    }

    public boolean add(BaseNode node) {
        if (nodeIds.add(node.getId())) {
            nodes.add(node);
            return true;
        }
        return false;
    }

    public Iterator<BaseNode> iterator() {
        return nodes.iterator();
    }

    public boolean contains(BaseNode node) {
        return nodeIds.contains(node.getId());
    }
}
