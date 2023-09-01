package org.drools.verifier.core.checks.base;

import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.api.Command;
import org.drools.verifier.api.StatusUpdate;
import org.drools.verifier.core.cache.inspectors.RuleInspector;

public class CheckRunManager {

    protected final Set<Check> rechecks = new HashSet<>();

    private final CheckRunner checkRunner;

    public CheckRunManager(final CheckRunner checkRunner) {
        this.checkRunner = checkRunner;
    }

    /**
     * Run analysis with feedback
     * @param onStatus Command executed repeatedly receiving status update
     * @param onCompletion Command executed on completion
     */
    public void run(final StatusUpdate onStatus,
                    final Command onCompletion) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //If there are no checks to run simply return
        if (rechecks.isEmpty()) {
            if (onCompletion != null) {
                onCompletion.execute();
                return;
            }
        }

        checkRunner.run(rechecks,
                        onStatus,
                        onCompletion);
        rechecks.clear();
    }

    public void addChecks(final Set<Check> checks) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        //Add new checks
        rechecks.addAll(checks);
    }

    public boolean isEmpty() {
        return rechecks.isEmpty();
    }

    public void remove(final RuleInspector removedRuleInspector) {
        //Ensure active analysis is cancelled
        cancelExistingAnalysis();

        final Set<Check> checks = removedRuleInspector.clearChecks();
        rechecks.removeAll(checks);
    }

    public void cancelExistingAnalysis() {
        checkRunner.cancelExistingAnalysis();
    }
}
