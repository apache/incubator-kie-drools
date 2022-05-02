package org.drools.ruleunits.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitInstanceFactory;

import static org.drools.ruleunits.impl.InterpretedRuleUnit.createRuleUnitKieProject;

public class InMemoryRuleUnitInstanceFactory extends RuleUnitInstanceFactory {

    private static final Map<Class, ClassLoader> kieModuleClassLoaders = new HashMap<>();

    public static <T extends RuleUnitData> RuleUnitInstance<T> generateAndInstance(T ruleUnit) {
        try {
            ClassLoader kieModuleClassLoader = kieModuleClassLoaders.computeIfAbsent(ruleUnit.getClass(), c -> createRuleUnitKieProject(c).getClassLoader());
            return createRuleUnitInstance( ruleUnit, Class.forName(getRuleUnitClassName(ruleUnit), true, kieModuleClassLoader) );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Do you forget to run code generation?", e);
        }
    }
}
