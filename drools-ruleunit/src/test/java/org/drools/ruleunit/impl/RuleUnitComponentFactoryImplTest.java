package org.drools.ruleunit.impl;

import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleUnitComponentFactoryImplTest {

    @Test
    public void createRuleUnitDescriptionFromClass() {
        RuleUnitComponentFactoryImpl r = new RuleUnitComponentFactoryImpl();
        KnowledgePackageImpl fooBar = new KnowledgePackageImpl("org.drools.ruleunit");
        assertNotNull(r.createRuleUnitDescription(fooBar, org.drools.ruleunit.TestRuleUnit.class));
    }

    @Test
    public void createRuleUnitDescriptionFromString() {
        RuleUnitComponentFactoryImpl r = new RuleUnitComponentFactoryImpl();
        KnowledgePackageImpl fooBar = new KnowledgePackageImpl("org.drools.ruleunit");
        assertNull(r.createRuleUnitDescription(fooBar, "org.drools.ruleunit.TestRuleUnit.class"));
    }
}