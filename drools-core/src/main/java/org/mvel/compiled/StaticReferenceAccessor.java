package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class StaticReferenceAccessor implements AccessorNode {
    private AccessorNode nextNode;

    Object literal;

    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vars) throws Exception {
        if (nextNode != null) {
            return nextNode.getValue(literal, elCtx, vars);
        }
        else {
            return literal;
        }
    }

    public Object getLiteral() {
        return literal;
    }

    public void setLiteral(Object literal) {
        this.literal = literal;
    }

    public StaticReferenceAccessor() {
    }

    public AccessorNode getNextNode() {
        return nextNode;
    }

    public AccessorNode setNextNode(AccessorNode nextNode) {
        return this.nextNode = nextNode;
    }

}
