package org.kie.kogito.incubation.rules;

import org.junit.jupiter.api.Test;
import org.kie.drl.api.identifiers.InstanceQueryId;
import org.kie.drl.api.identifiers.QueryId;
import org.kie.drl.api.identifiers.RuleUnitId;
import org.kie.drl.api.identifiers.RuleUnitIdParser;
import org.kie.drl.api.identifiers.RuleUnitInstanceId;

import static org.junit.jupiter.api.Assertions.assertEquals;

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