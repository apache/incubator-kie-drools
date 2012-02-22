package org.drools.rule.constraint;

import java.util.Map;

public interface MapConditionEvaluator {
    boolean evaluate(Object object, Map<String, Object> vars);
}
