/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NodeSet implements Iterable<BaseNode> {
    private final List<BaseNode> nodes = new ArrayList<BaseNode>();
    private final Set<Integer> nodeIds = new HashSet<Integer>();

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
