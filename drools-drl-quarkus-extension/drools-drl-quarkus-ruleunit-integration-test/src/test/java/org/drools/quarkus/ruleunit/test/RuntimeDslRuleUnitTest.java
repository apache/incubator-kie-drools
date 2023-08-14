package org.drools.quarkus.ruleunit.test;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RuntimeDslRuleUnitTest {
    
    @Inject
    RuleUnit<SimpleDTUnit> ruleUnit;

    @Test
    public void testR1() {
        SimpleDTUnit unitData = new SimpleDTUnit();
        unitData.getAge().set( 19 );
        unitData.getIncidents().set( false );

        RuleUnitInstance<SimpleDTUnit> unitInstance = ruleUnit.createInstance(unitData);

        unitInstance.fire();

        assertThat( unitData.getBasePrice() ).hasValue( 800 );
    }
}