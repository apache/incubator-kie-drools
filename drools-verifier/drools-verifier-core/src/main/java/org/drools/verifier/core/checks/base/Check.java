package org.drools.verifier.core.checks.base;

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.configuration.CheckConfiguration;

public interface Check {

    boolean check();

    Issue getIssue();

    boolean hasIssues();

    boolean isActive(final CheckConfiguration checkConfiguration);
}
