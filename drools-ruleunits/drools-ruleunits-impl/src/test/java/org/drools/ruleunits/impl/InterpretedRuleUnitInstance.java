package org.drools.ruleunits.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.EntryPoint;

public class InterpretedRuleUnitInstance<T extends RuleUnitData> extends ReteEvaluatorBasedRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, ReteEvaluator reteEvaluator, RuleConfig ruleConfig) {
        super(unit, workingMemory, reteEvaluator, ruleConfig);
    }

    protected void bind(ReteEvaluator reteEvaluator, T workingMemory) {
        try {
            for (PropertyDescriptor prop : Introspector.getBeanInfo(workingMemory.getClass()).getPropertyDescriptors()) {
                Field f;
                try {
                    f = workingMemory.getClass().getDeclaredField(prop.getName());
                } catch (NoSuchFieldException noSuchFieldException) {
                    // ignore not existing fields
                    continue;
                }
                f.setAccessible(true);
                Object v = f.get(workingMemory);
                String dataSourceName = f.getName();
                if (v instanceof DataSource) {
                    DataSource<?> o = (DataSource<?>) v;
                    EntryPoint ep = reteEvaluator.getEntryPoint(dataSourceName);
                    o.subscribe(new EntryPointDataProcessor(ep));
                }
                try {
                    reteEvaluator.setGlobal(dataSourceName, v);
                } catch (RuntimeException e) {
                    // ignore if the global doesn't exist
                }
            }
        } catch (IntrospectionException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
