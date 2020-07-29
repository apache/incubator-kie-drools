package org.kie.kogito.codegen.prediction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionRuleMapperGeneratorTest {

    @Test
    void getPredictionRuleMapperSourceWithoutPackage() {
        final String fullRuleName = "FULL_RULE_NAME";
        String retrieved = PredictionRuleMapperGenerator.getPredictionRuleMapperSource(fullRuleName);
        assertNotNull(retrieved);
        String expected = String.format("public final static String ruleName = \"%s\";", fullRuleName);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void getPredictionRuleMapperSourceWithPackage() {
        final String packageName = "PACKAGE";
        final String ruleName = "RULE_NAME";
        final String fullRuleName = packageName + "." + ruleName;
        String retrieved = PredictionRuleMapperGenerator.getPredictionRuleMapperSource(fullRuleName);
        assertNotNull(retrieved);
        String expected = String.format("package %s;", packageName);
        assertTrue(retrieved.contains(expected));
        expected = String.format("public final static String ruleName = \"%s\";", fullRuleName);
        assertTrue(retrieved.contains(expected));
    }
}