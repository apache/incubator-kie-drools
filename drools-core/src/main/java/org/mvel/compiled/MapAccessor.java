package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

import java.util.Map;

public class MapAccessor implements AccessorNode {
    private AccessorNode nextNode;

    private Object property;

    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vrf) throws Exception {
        if (nextNode != null) {
            return nextNode.getValue(((Map)ctx).get(property), elCtx, vrf);
        }
        else {
            return ((Map)ctx).get(property);
        }
    }

    public Object getProperty() {
        return property;
    }

    public void setProperty(Object property) {
        this.property = property;
    }

    public AccessorNode getNextNode() {
        return nextNode;
    }

    public AccessorNode setNextNode(AccessorNode nextNode) {
        return this.nextNode = nextNode;
    }


    public String toString() {
        return "Map Accessor -> [" + property + "]";
    }
}
