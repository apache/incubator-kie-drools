package org.drools.core.reteoo;

import org.drools.core.reteoo.builder.BuildContext;

import java.util.HashMap;
import java.util.Map;

public class MethodCountingLeftInputAdapterNode extends LeftInputAdapterNode {
    protected Map<String, Integer> methodCount;

    public MethodCountingLeftInputAdapterNode(final int id,
                                final ObjectSource source,
                                final BuildContext context) {
        super(id, source, context);
    }

    public boolean equals(final Object object) {
        incrementCount("equals");
        return super.equals(object);
    }

    public boolean thisNodeEquals(final Object object) {
        incrementCount("thisNodeEquals");
        return super.thisNodeEquals(object);
    }

    private void incrementCount(String key) {
        if ( this.methodCount== null ) {
            this.methodCount = new HashMap<String, Integer>();
        }
        int count = methodCount.containsKey(key) ? methodCount.get(key) : 0;
        methodCount.put(key, count + 1);
    }

    public Map<String, Integer> getMethodCountMap() {
        return this.methodCount;
    }
}
