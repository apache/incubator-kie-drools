package org.drools.verifier.core.checks.base;

import java.util.List;

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.configuration.CheckConfiguration;

/**
 * Wraps more than one check into one.
 * Each check will look for failure in the given order.
 * Once failure is found the rest of the checks are ignored.
 * <br>
 * <br>
 * This is used for example by the conflict-subsubsumption-redundancy chain.
 * Where conflict, when found, blocks subsumption.
 */
public class PriorityListCheck
        implements Check {

    private final List<Check> filteredSet;

    private Check checkWithIssues;

    public PriorityListCheck(final List<Check> filteredSet) {
        this.filteredSet = filteredSet;
    }

    @Override
    public Issue getIssue() {
        return checkWithIssues.getIssue();
    }

    @Override
    public boolean hasIssues() {
        return checkWithIssues != null;
    }

    @Override
    public boolean isActive(final CheckConfiguration checkConfiguration) {
        return !filteredSet.isEmpty();
    }

    @Override
    public boolean check() {
        checkWithIssues = filteredSet.stream().filter(Check::check).findFirst().orElse(null);
        return checkWithIssues != null;
    }
}
