package org.drools.mvel.integrationtests.facts;

public class ChildFact2 {

    private final int id;
    private final int parentId;

    public ChildFact2(final int id, final int parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }
}
