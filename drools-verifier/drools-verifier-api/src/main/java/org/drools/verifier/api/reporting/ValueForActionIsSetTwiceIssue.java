package org.drools.verifier.api.reporting;

import java.util.Set;

public class ValueForActionIsSetTwiceIssue
        extends Issue {

    private String firstItem;
    private String secondItem;

    public ValueForActionIsSetTwiceIssue() {
    }

    public ValueForActionIsSetTwiceIssue(final Severity severity,
                                         final CheckType checkType,
                                         final String firstItem,
                                         final String secondItem,
                                         final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public void setFirstItem(final String firstItem) {
        this.firstItem = firstItem;
    }

    public void setSecondItem(final String secondItem) {
        this.secondItem = secondItem;
    }

    public String getFirstItem() {
        return firstItem;
    }

    public String getSecondItem() {
        return secondItem;
    }
}
