package org.drools.verifier.core.checks.base;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CheckRunManagerRepeatingCommandTest {

    private HashSet<Check> checksToRun;

    @BeforeEach
    public void setUp() throws Exception {
        checksToRun = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            checksToRun.add(mock(Check.class));
        }
    }

    @Test
    void testRunAll() throws Exception {
        final ChecksRepeatingCommand checksRepeatingCommand = new ChecksRepeatingCommand(checksToRun,
                null,
                null);

        while (checksRepeatingCommand.execute()) {
            // Loopidiloop
        }

        final Check[] array = checksToRun.toArray(new Check[checksToRun.size()]);
        for (int i = 0; i < 100; i++) {
            verify(array[i]).check();
        }
    }
}