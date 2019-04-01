package org.kie.submarine.rules.impl;

import java.lang.reflect.Field;

import org.kie.api.runtime.KieSession;
import org.kie.submarine.rules.RuleUnit;
import org.kie.submarine.rules.RuleUnitInstance;

public class AbstractRuleUnitInstance<T> implements RuleUnitInstance<T> {

    private final T workingMemory;
    private final RuleUnit<T> unit;
    private final KieSession rt;

    public AbstractRuleUnitInstance(RuleUnit<T> unit, T workingMemory, KieSession rt) {
        this.unit = unit;
        this.rt = rt;
        this.workingMemory = workingMemory;
    }

    public void fire() {
        magicReflectionThingie(rt, workingMemory);
        rt.fireAllRules();
    }

    @Override
    public RuleUnit<T> unit() {
        return unit;
    }

    public T workingMemory() {
        return workingMemory;
    }

    private void magicReflectionThingie(KieSession rt, T workingMemory) {
        try {
            for (Field f : workingMemory.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = null;
                v = f.get(workingMemory);
                if (v instanceof ListDataSource) {
                    ListDataSource o = (ListDataSource) v;
                    o.drainInto(rt::insert);
                }
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
