package org.drools.modelcompiler.builder.generator;

import org.junit.Test;

import static org.drools.modelcompiler.builder.generator.Consequence.containsWord;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsequenceTest {

    @Test
    public void containsWordTest() throws Exception {
        assertFalse(containsWord("$cheesery", "results.add($cheeseryResult);\n"));
        assertTrue(containsWord("$cheeseryResult", "results.add($cheeseryResult);\n"));
        assertFalse(containsWord("cheesery", "results.add($cheesery);\n"));
    }
}