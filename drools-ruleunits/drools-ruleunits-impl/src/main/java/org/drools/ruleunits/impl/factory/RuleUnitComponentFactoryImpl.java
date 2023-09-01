package org.drools.ruleunits.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.GeneratedRuleUnitDescription;
import org.drools.ruleunits.impl.ReflectiveRuleUnitDescription;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitComponentFactoryImpl implements RuleUnitComponentFactory {

    private final Map<String, GeneratedRuleUnitDescription> generatedRuleUnitDescriptions = new HashMap<>();

    public void registerRuleUnitDescription(GeneratedRuleUnitDescription ruleUnitDescription) {
        generatedRuleUnitDescriptions.put(ruleUnitDescription.getCanonicalName(), ruleUnitDescription);
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, Class<?> ruleUnitClass) {
        return new ReflectiveRuleUnitDescription((Class<? extends RuleUnitData>) ruleUnitClass);
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, String ruleUnitSimpleName) {
        return generatedRuleUnitDescriptions.get(pkg.getName() + '.' + ruleUnitSimpleName);
    }

    @Override
    public boolean isRuleUnitClass(Class<?> ruleUnitClass) {
        return RuleUnitData.class.isAssignableFrom(ruleUnitClass);
    }

    @Override
    public boolean isDataSourceClass(Class<?> ruleUnitClass) {
        return DataSource.class.isAssignableFrom(ruleUnitClass);
    }
}
