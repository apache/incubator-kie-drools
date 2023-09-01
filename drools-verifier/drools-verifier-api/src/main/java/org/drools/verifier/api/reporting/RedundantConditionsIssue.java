package org.drools.verifier.api.reporting;

import java.util.Set;

public class RedundantConditionsIssue
        extends Issue {

    private String factType;
    private String name;
    private String firstItem;
    private String secondItem;

    public RedundantConditionsIssue() {
    }

    public RedundantConditionsIssue(final Severity severity,
                                    final CheckType checkType,
                                    final String factType,
                                    final String name,
                                    final String firstItem,
                                    final String secondItem,
                                    final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.factType = factType;
        this.name = name;
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public void setFactType(final String factType) {
        this.factType = factType;
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

    public String getFactType() {
        return factType;
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
