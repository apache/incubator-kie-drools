package org.drools.verifier.api.reporting;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IllegalVerifierStateIssueTest {

    @Test
    void defaults() {

        final IllegalVerifierStateIssue issue = new IllegalVerifierStateIssue();

        assertThat(issue.getSeverity()).isEqualTo(Severity.ERROR);
        assertThat(issue.getCheckType()).isEqualTo(CheckType.ILLEGAL_VERIFIER_STATE);
        assertThat(issue.getRowNumbers()).isEmpty();
    }
}