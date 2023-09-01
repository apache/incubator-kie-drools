package org.drools.ruleunits.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnits;

public abstract class AbstractRuleUnits implements RuleUnits {

    private Map<String, RuleUnitInstance<?>> unitRegistry = new HashMap<>();

    @Override
    public <T extends RuleUnitData> RuleUnit<T> create(Class<T> clazz) {
        return (RuleUnit<T>) create(clazz.getCanonicalName());
    }

    protected abstract RuleUnit<?> create(String fqcn);

    @Override
    public void register(String name, RuleUnitInstance<?> unitInstance) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot register a RuleUnitInstance with a null name");
        }
        unitRegistry.put(name, unitInstance);
    }

    @Override
    public RuleUnitInstance<?> getRegisteredInstance(String name) {
        return unitRegistry.get(name);
    }


    public static class DummyRuleUnits extends AbstractRuleUnits {

        public static final DummyRuleUnits INSTANCE = new DummyRuleUnits();

        @Override
        protected RuleUnit<?> create(String fqcn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void register(RuleUnit<?> unit) {
            // ignore
        }
    }
}
