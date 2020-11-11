/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.rules;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.rules.units.InterpretedRuleUnit;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
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