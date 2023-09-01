package org.drools.ruleunits.dsl;

import java.util.Map;
import java.util.Set;

import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.dsl.util.RuleDefinition;

public class UnitGlobalsResolver {

    private final Map<String, RuleDefinition.FieldDefinition> fieldByGlobal;

    public UnitGlobalsResolver(Map<String, RuleDefinition.FieldDefinition> fieldByGlobal) {
        this.fieldByGlobal = fieldByGlobal;
    }

    public Set<String> getGlobalNames() {
        return fieldByGlobal.keySet();
    }

    public Object resolveGlobalObject(RuleUnitData unit, String name) {
        return fieldByGlobal.get(name).get(unit);
    }
}
