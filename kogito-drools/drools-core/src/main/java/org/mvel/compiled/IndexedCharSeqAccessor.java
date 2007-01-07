package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

import java.util.Map;
import java.util.List;

public class IndexedCharSeqAccessor implements AccessorNode {
    private AccessorNode nextNode;

    private int index;

    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vars) throws Exception {
        if (nextNode != null) {
            return nextNode.getValue(((String)ctx).charAt(index), elCtx, vars);
        }
        else {
            return ((String)ctx).charAt(index);
        }
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AccessorNode getNextNode() {
        return nextNode;
    }

    public AccessorNode setNextNode(AccessorNode nextNode) {
        return this.nextNode = nextNode;
    }


    public String toString() {
        return "Array Accessor -> [" + index + "]";
    }
}
