package org.drools.base.base;

import org.kie.api.runtime.rule.Variable;

public interface DroolsQuery {
    Variable[] getVariables();

    Object getName();

    public Object[] getElements();
}
