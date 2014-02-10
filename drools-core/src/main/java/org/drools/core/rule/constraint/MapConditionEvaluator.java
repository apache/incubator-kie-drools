package org.drools.core.rule.constraint;

import org.drools.core.common.InternalFactHandle;

import java.util.Map;

public interface MapConditionEvaluator {
    boolean evaluate(InternalFactHandle handle, Map<String, Object> vars);
}
