package org.drools.rule.builder.dialect;

import org.junit.Test;

import static org.drools.rule.builder.dialect.DialectUtil.normalizeRuleName;
import static org.junit.Assert.assertEquals;

public class DialectUtilTest {

    @Test
    public void testNormalizeRuleName() {
        assertEquals("Rule_a", normalizeRuleName("Rule a"));
        assertEquals("Rule_$u62$", normalizeRuleName("Rule >"));
        assertEquals("Rule_$u60$", normalizeRuleName("Rule <"));
        assertEquals("Rule__dollar_$u60$$u62$", normalizeRuleName("Rule $<>"));
    }
}
