package org.drools.model.codegen.execmodel.generator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.generator.Consequence.containsWord;

public class ConsequenceTest {

    @Test
    public void containsWordTest() throws Exception {
        assertThat(containsWord("$cheesery", "results.add($cheeseryResult);\n")).isFalse();
        assertThat(containsWord("$cheeseryResult", "results.add($cheeseryResult);\n")).isTrue();
        assertThat(containsWord("cheesery", "results.add($cheesery);\n")).isFalse();
    }
}