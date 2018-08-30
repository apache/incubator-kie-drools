package org.drools.modelcompiler.builder.generator;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.drools.modelcompiler.builder.generator.Consequence.containsWord;

public class ConsequenceTest {

    @Test
    public void containsWordTest() throws Exception {
        assertFalse(containsWord("$cheesery", "results.add($cheeseryResult);\n"));
        assertTrue(containsWord("$cheeseryResult", "results.add($cheeseryResult);\n"));
        assertFalse(containsWord("cheesery", "results.add($cheesery);\n"));
    }
}