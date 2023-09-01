package org.drools.verifier.core.main;

import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;

public interface Reporter {

    void sendReport(final Set<Issue> issues);

    void sendStatus(final Status status);
}
