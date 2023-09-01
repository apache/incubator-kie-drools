package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OOPathTest {

    @Test
    public void testOOPathAfterNot() {
        OOPathTestUnit unit = new OOPathTestUnit();
        unit.getStrings().add("Hello World");

        RuleUnitInstance<OOPathTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);
        assertEquals(1, unitInstance.fire());
        assertTrue(unit.getResults().contains("it worked!"));
    }
}
