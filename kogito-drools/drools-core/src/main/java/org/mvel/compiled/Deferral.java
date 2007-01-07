package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

import java.util.Map;

public class Deferral implements AccessorNode {
    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vr) throws Exception {
        return null;
    }

    public AccessorNode getNextNode() {
        return null;
    }

    public AccessorNode setNextNode(AccessorNode accessorNode) {
        return null;
    }
}
