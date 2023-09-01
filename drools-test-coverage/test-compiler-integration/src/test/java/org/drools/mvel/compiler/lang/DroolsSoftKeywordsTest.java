package org.drools.mvel.compiler.lang;

import org.drools.drl.parser.lang.DroolsSoftKeywords;
import org.drools.compiler.builder.impl.EvaluatorRegistry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DroolsSoftKeywordsTest {

    /**
     * Test method for {@link org.kie.lang.DroolsSoftKeywords#isOperator(java.lang.String, boolean)}.
     */
    @Test
    public void testIsOperator() {
        // initializes the registry
        new EvaluatorRegistry();

        // test the registry
        assertThat(DroolsSoftKeywords.isOperator("matches", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("matches", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("contains", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("contains", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("after", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("after", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("before", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("before", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("finishes", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("finishes", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("overlappedby", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("overlappedby", true)).isTrue();

        assertThat(DroolsSoftKeywords.isOperator("xyz", false)).isFalse();
        assertThat(DroolsSoftKeywords.isOperator("xyz", true)).isFalse();

    }

}
