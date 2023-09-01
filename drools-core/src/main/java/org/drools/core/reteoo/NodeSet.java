package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
