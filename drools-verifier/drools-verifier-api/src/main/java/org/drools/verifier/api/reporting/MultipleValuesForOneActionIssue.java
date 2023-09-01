package org.drools.verifier.api.reporting;

import java.util.Set;

public class MultipleValuesForOneActionIssue
        extends Issue {

    private String conflictedItem;
    private String conflictingItem;

    public MultipleValuesForOneActionIssue() {
    }

    public MultipleValuesForOneActionIssue(final Severity severity,
                                           final CheckType checkType,
                                           final String conflictedItem,
                                           final String conflictingItem,
                                           final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);

        this.conflictedItem = conflictedItem;
        this.conflictingItem = conflictingItem;
    }

    public void setConflictedItem(final String conflictedItem) {
        this.conflictedItem = conflictedItem;
    }

    public void setConflictingItem(final String conflictingItem) {
        this.conflictingItem = conflictingItem;
    }

    public String getConflictedItem() {
        return conflictedItem;
    }

    public String getConflictingItem() {
        return conflictingItem;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
