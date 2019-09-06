package org.kie.kogito.rules;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpretedRuleUnitTest {
    @Test
    public void fireRules() {
        HelloWorld workingMemory = new HelloWorld();
        RuleUnit<HelloWorld> ruleUnit = InterpretedRuleUnit.of(HelloWorld.class);
        RuleUnitInstance<HelloWorld> instance = ruleUnit.createInstance(workingMemory);
        ArrayList<String> messages = new ArrayList<>();
        workingMemory.getStrings().subscribe(DataObserver.of(messages::add));
        instance.fire();

        assertTrue(messages.isEmpty());

        workingMemory.getStrings().add("Hello World");
        assertEquals(1, messages.size());

        instance.fire();
        assertEquals(2, messages.size());
    }
}