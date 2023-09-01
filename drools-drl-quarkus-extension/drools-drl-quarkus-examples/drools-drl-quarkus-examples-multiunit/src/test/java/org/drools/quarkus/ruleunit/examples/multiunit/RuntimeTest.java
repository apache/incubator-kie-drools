package org.drools.quarkus.ruleunit.examples.multiunit;

import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class RuntimeTest {

    @Inject
    RuleUnit<FirstUnit> firstUnit;

    @Inject
    RuleUnit<SecondUnit> secondUnit;

    @Test
    public void testFirst() {
        FirstUnit first = new FirstUnit();
        RuleUnitInstance<FirstUnit> instance = firstUnit.createInstance(first);

        AtomicReference<RuleOutput1> output = new AtomicReference<>();

        first.getOutput().subscribe(new DataProcessor<RuleOutput1>() {
            @Override
            public FactHandle insert(DataHandle handle, RuleOutput1 object) {
                output.set(object);
                return null;
            }

            @Override
            public void update(DataHandle handle, RuleOutput1 object) {

            }

            @Override
            public void delete(DataHandle handle) {

            }
        });

        first.getInput().append(new RuleInput("Hello"));

        instance.fire();

        assertEquals("Hi 1", output.get().getText());
    }

    @Test
    public void testSecond() {
        SecondUnit second = new SecondUnit();
        RuleUnitInstance<SecondUnit> instance = secondUnit.createInstance(second);

        AtomicReference<RuleOutput2> output = new AtomicReference<>();

        second.getOutput().subscribe(new DataProcessor<RuleOutput2>() {
            @Override
            public FactHandle insert(DataHandle handle, RuleOutput2 object) {
                output.set(object);
                return null;
            }

            @Override
            public void update(DataHandle handle, RuleOutput2 object) {

            }

            @Override
            public void delete(DataHandle handle) {

            }
        });

        second.getInput().append(new RuleInput("Hello"));

        instance.fire();

        assertEquals("Hi 2", output.get().getText());
    }
}
