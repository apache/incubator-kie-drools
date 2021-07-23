package org.mvel2.tests.core;

import java.util.Map;

public class EvaluatorTemplate implements org.drools.mvel2.CompiledJavaEvaluator {

    @Override
    public Object eval(java.util.Map map) {
        // declarations are on top
        int usedBinding;

        // binding assignment
        {
            usedBinding = (int) map.get("usedBinding");
        }

        // execute MVEL here
        {

        }

        // repopulate map
        {
            map.put("usedBinding", usedBinding);
        }

        return usedBinding;
    }
}
