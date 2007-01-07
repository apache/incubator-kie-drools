package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class GetterAccessor implements AccessorNode {
    private AccessorNode nextNode;

    private final Method method;

    public static final Object[] EMPTY = new Object[0];

    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vars) throws Exception {
        if (nextNode != null) {
            return nextNode.getValue(method.invoke(ctx, EMPTY), elCtx, vars);
        }
        else {
            return method.invoke(ctx, EMPTY);
        }
    }


    public GetterAccessor(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public AccessorNode setNextNode(AccessorNode nextNode) {
        return this.nextNode = nextNode;
    }

    public AccessorNode getNextNode() {
        return nextNode;
    }

    public String toString() {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
