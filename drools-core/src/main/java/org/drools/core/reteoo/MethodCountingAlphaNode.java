package org.drools.core.reteoo;

import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.AlphaNodeFieldConstraint;

import java.util.HashMap;
import java.util.Map;

public class MethodCountingAlphaNode extends AlphaNode {
    protected Map<String, Integer> methodCount;

    public MethodCountingAlphaNode(final int id,
                     final AlphaNodeFieldConstraint constraint,
                     final ObjectSource objectSource,
                     final BuildContext context) {
        super(id, constraint, objectSource, context);
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
            this.methodCount = new HashMap<>();
        }
        int count = methodCount.containsKey(key) ? methodCount.get(key) : 0;
        methodCount.put(key, count + 1);
    }

    public Map<String, Integer> getMethodCountMap() {
        return this.methodCount;
    }
}
