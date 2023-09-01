package org.drools.verifier.core.checks.base;

import java.util.Set;

import org.drools.verifier.api.Command;
import org.drools.verifier.api.StatusUpdate;

/**
 * Just a start for server side runner at the moment. Used by JUnit tests.
 */
public class JavaCheckRunner
        implements CheckRunner {

    @Override
    public void run(final Set<Check> rechecks,
                    final StatusUpdate onStatus,
                    final Command onCompletion) {
        ChecksRepeatingCommand command = new ChecksRepeatingCommand(rechecks,
                                                                    onStatus,
                                                                    onCompletion);
        while (command.execute()) {

        }
    }

    @Override
    public void cancelExistingAnalysis() {
        // All or nothing
    }
}
