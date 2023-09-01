package org.drools.verifier.api;

import java.util.Set;

import org.drools.verifier.api.reporting.Issue;

public interface Reporter {

    void sendReport(final Set<Issue> issues);

    void sendStatus(final Status status);

    void activate();
}
