package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class RuleUnitIds implements ComponentRoot {
    public RuleUnitId get(Class<?> ruleUnitDefinition) {
        return get(ruleUnitDefinition.getCanonicalName());
    }

    public RuleUnitId get(String canonicalName) {
        return new RuleUnitId(canonicalName);
    }
}
