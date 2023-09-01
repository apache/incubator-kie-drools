package org.drools.verifier.api.reporting;

import java.util.Set;

public class ValueForFactFieldIsSetTwiceIssue
        extends Issue {

    private String boundName;
    private String name;
    private String firstItem;
    private String secondItem;

    public ValueForFactFieldIsSetTwiceIssue() {
    }

    public ValueForFactFieldIsSetTwiceIssue(final Severity severity,
                                            final CheckType checkType,
                                            final String boundName,
                                            final String name,
                                            final String firstItem,
                                            final String secondItem,
                                            final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.boundName = boundName;
        this.name = name;
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public void setBoundName(final String boundName) {
        this.boundName = boundName;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setFirstItem(final String firstItem) {
        this.firstItem = firstItem;
    }

    public void setSecondItem(final String secondItem) {
        this.secondItem = secondItem;
    }

    public String getBoundName() {
        return boundName;
    }

    public String getName() {
        return name;
    }

    public String getFirstItem() {
        return firstItem;
    }

    public String getSecondItem() {
        return secondItem;
    }
}
