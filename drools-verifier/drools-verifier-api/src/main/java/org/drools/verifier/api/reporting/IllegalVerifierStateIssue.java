package org.drools.verifier.api.reporting;

import java.util.Collections;

public class IllegalVerifierStateIssue
        extends Issue {

    public IllegalVerifierStateIssue() {
        super(Severity.ERROR,
              CheckType.ILLEGAL_VERIFIER_STATE,
              Collections.EMPTY_SET
        );
    }
}
