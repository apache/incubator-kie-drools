package org.kie.pmml.pmml_4_2.rules;

import java.util.Collection;

import org.drools.model.Rule;

public interface RuleProvider {

    public Collection<String> getProvidedRuleNames();

    public Rule getRule(String ruleName);
}
