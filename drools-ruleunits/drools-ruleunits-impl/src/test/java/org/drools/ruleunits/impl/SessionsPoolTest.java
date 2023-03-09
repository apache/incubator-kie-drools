/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionsPoolTest {

    @Test
    public void sessionsPool() {
        SessionsPoolUnit unit = new SessionsPoolUnit();
        unit.getStrings().add("Hello World");

        RuleUnit<SessionsPoolUnit> ruleUnit = RuleUnitProvider.get().getRuleUnit(unit);

        try ( RuleUnitInstance<SessionsPoolUnit> unitInstance = ruleUnit.createInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }
}
