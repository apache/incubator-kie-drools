package org.mvel;

import org.mvel.integration.VariableResolverFactory;

import java.util.Map;

public interface AccessorNode {
    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory variableFactory) throws Exception;
    public AccessorNode getNextNode();
    public AccessorNode setNextNode(AccessorNode accessorNode);
}
