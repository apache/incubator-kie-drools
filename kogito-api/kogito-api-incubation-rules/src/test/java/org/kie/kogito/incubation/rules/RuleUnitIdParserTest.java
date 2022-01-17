/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.incubation.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleUnitIdParserTest {

    @Test
    void parseRuleUnitId() {
        assertEquals(RuleUnitId.class, RuleUnitIdParser.parse("/rule-units/u").getClass());
        assertEquals("u", RuleUnitIdParser.parse("/rule-units/u", RuleUnitId.class).ruleUnitId());
    }

    @Test
    void parseQueryId() {
        assertEquals(QueryId.class, RuleUnitIdParser.parse("/rule-units/u/queries/q").getClass());
        assertEquals("u", RuleUnitIdParser.parse("/rule-units/u/queries/q", RuleUnitId.class).ruleUnitId());
        assertEquals("q", RuleUnitIdParser.parse("/rule-units/u/queries/q", QueryId.class).queryId());
    }

    @Test
    void parseRuleUnitInstanceId() {
        assertEquals(RuleUnitInstanceId.class, RuleUnitIdParser.parse("/rule-units/u/instances/ui").getClass());
        assertEquals("u", RuleUnitIdParser.parse("/rule-units/u/instances/ui", RuleUnitId.class).ruleUnitId());
        assertEquals("ui", RuleUnitIdParser.parse("/rule-units/u/instances/ui", RuleUnitInstanceId.class).ruleUnitInstanceId());
    }

    @Test
    void parseInstanceQueryId() {
        assertEquals(InstanceQueryId.class, RuleUnitIdParser.parse("/rule-units/u/instances/ui/queries/q").getClass());
        assertEquals("u", RuleUnitIdParser.parse("/rule-units/u/instances/ui/queries/q", RuleUnitId.class).ruleUnitId());
        assertEquals("ui", RuleUnitIdParser.parse("/rule-units/u/instances/ui/queries/q", RuleUnitInstanceId.class).ruleUnitInstanceId());
        assertEquals("q", RuleUnitIdParser.parse("/rule-units/u/instances/ui/queries/q", InstanceQueryId.class).queryId());
    }

}