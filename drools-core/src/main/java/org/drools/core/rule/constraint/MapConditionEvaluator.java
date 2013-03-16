package org.drools.core.rule.constraint;

import java.util.Map;

public interface MapConditionEvaluator {
    boolean evaluate(Object object, Map<String, Object> vars);
}
