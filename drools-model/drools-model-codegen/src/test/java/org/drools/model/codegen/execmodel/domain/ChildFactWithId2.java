package org.drools.model.codegen.execmodel.domain;

public class ChildFactWithId2 {

    private final int id;
    private final int parentId;

    public ChildFactWithId2(final int id, final int parentId) {
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
