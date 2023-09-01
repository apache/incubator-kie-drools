package org.drools.quarkus.ruleunit.test;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class RuntimeTest {

    @Inject
    RuleUnit<HelloWorldUnit> ruleUnit;

    @Test
    public void testRuleUnit() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Mario");

        try ( RuleUnitInstance<HelloWorldUnit> instance = ruleUnit.createInstance(unit)  ) {
            instance.fire();
        }

        assertEquals(1, unit.getResults().size());
        assertEquals("Hello Mario", unit.getResults().get(0));
    }
}
