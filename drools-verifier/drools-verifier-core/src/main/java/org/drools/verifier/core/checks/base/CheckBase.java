package org.drools.verifier.core.checks.base;

import java.util.Optional;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.util.PortablePreconditions;

public abstract class CheckBase
        implements Check {

    protected final AnalyzerConfiguration configuration;

    protected boolean hasIssues = false;

    public CheckBase(final AnalyzerConfiguration configuration) {
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    @Override
    public boolean hasIssues() {
        return hasIssues;
    }

    @Override
    public final Issue getIssue() {
        return makeIssue(resolveSeverity(),
                         getCheckType());
    }

    protected abstract Issue makeIssue(final Severity severity,
                                       final CheckType checkType);

    protected abstract CheckType getCheckType();

    protected abstract Severity getDefaultSeverity();

    @Override
    public boolean isActive(final CheckConfiguration checkConfiguration) {
        return checkConfiguration.getCheckConfiguration()
                .contains(getCheckType());
    }

    protected Severity resolveSeverity() {
        final Optional<Severity> severityOverwrite = configuration.getCheckConfiguration()
                .getSeverityOverwrite(getCheckType());

        if (severityOverwrite.isPresent()) {
            return severityOverwrite.get();
        } else {
            return getDefaultSeverity();
        }
    }
}
