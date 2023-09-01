package org.drools.verifier.core.checks.base;

import java.util.Set;

import org.drools.verifier.api.Command;
import org.drools.verifier.api.StatusUpdate;

public interface CheckRunner {

    void run(Set<Check> rechecks,
             StatusUpdate onStatus,
             Command onCompletion);

    void cancelExistingAnalysis();
}
