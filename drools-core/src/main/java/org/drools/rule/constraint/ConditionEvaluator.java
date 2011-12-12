package org.drools.rule.constraint;

import java.util.Map;

public interface ConditionEvaluator {
    boolean evaluate(Object object);
    boolean evaluate(Object object, Map<String, Object> vars);
}
