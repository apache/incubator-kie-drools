package org.drools.compiler.integrationtests.incrementalcompilation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class CasesFromGeneratedRulesTest {

    @Test
    @Timeout(40000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRulesRevertedRules() {
        String rule1 = """
                package com.rules;
                global java.util.List list
                rule R1
                 when
                  Integer()
                 then
                 list.add('R1');
                end
                """;

        String rule2 = """
                package com.rules;
                global java.util.List list
                rule R2
                 when
                  Integer()
                 exists(Integer() and Integer())
                 exists(Integer() and Integer())
                 then
                 list.add('R2');
                end
                """;

        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, 1, 2);
    }
}
