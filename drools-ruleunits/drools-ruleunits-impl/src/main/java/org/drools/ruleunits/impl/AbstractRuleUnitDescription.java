package org.drools.ruleunits.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.api.conf.RuleUnitConfig;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

public abstract class AbstractRuleUnitDescription implements RuleUnitDescription {

    private final Map<String, RuleUnitVariable> varDeclarations = new HashMap<>();
    private RuleUnitConfig config;

    @Override
    public Optional<Class<?>> getDatasourceType(String name) {
        return Optional.ofNullable(varDeclarations.get(name))
                .filter(RuleUnitVariable::isDataSource)
                .map(RuleUnitVariable::getDataSourceParameterType);
    }

    @Override
    public Optional<Type> getVarType(String name) {
        return Optional.ofNullable(varDeclarations.get(name)).map(RuleUnitVariable::getType);
    }

    @Override
    public boolean hasVar(String name) {
        return varDeclarations.containsKey(name);
    }

    @Override
    public RuleUnitVariable getVar(String name) {
        RuleUnitVariable ruleUnitVariable = varDeclarations.get(name);
        if (ruleUnitVariable == null) {
            throw new UndefinedRuleUnitVariableException(name, this.getCanonicalName());
        }
        return ruleUnitVariable;
    }

    @Override
    public Collection<String> getUnitVars() {
        return varDeclarations.keySet();
    }

    @Override
    public Collection<RuleUnitVariable> getUnitVarDeclarations() {
        return varDeclarations.values();
    }

    @Override
    public boolean hasDataSource(String name) {
        RuleUnitVariable ruleUnitVariable = varDeclarations.get(name);
        return ruleUnitVariable != null && ruleUnitVariable.isDataSource();
    }

    protected void putRuleUnitVariable(RuleUnitVariable varDeclaration) {
        varDeclarations.put(varDeclaration.getName(), varDeclaration);
    }

    protected void setConfig(RuleUnitConfig config) {
        this.config = config;
    }

    public RuleUnitConfig getConfig() {
        return config;
    }

    @Override
    public ClockTypeOption getClockType() {
        return (config.getDefaultedClockType() == org.drools.ruleunits.api.conf.ClockType.PSEUDO) ? ClockTypeOption.PSEUDO : ClockTypeOption.REALTIME;
    }

    @Override
    public Collection<KieBaseOption> getKieBaseOptions() {
        List<KieBaseOption> kieBaseOptions = new ArrayList<>();

        EventProcessingOption eventProcessingOption = (config.getDefaultedEventProcessingType() == EventProcessingType.CLOUD) ? EventProcessingOption.CLOUD : EventProcessingOption.STREAM;
        kieBaseOptions.add(eventProcessingOption);

        // Add any KieBaseOptions if available

        return kieBaseOptions;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleUnitDescription && getRuleUnitName().equals(((RuleUnitDescription) obj).getRuleUnitName());
    }

    @Override
    public int hashCode() {
        return getRuleUnitName().hashCode();
    }
}
