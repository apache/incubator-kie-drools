package org.drools.model.codegen.execmodel.domain;

public class SubFact {

    private int id;
    private int parentId;
    private Boolean indicator;

    public SubFact() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Boolean getIndicator() {
        if (indicator == null) {
            return false;
        } else {
            return indicator;
        }
    }

    public void setIndicator(Boolean indicator) {
        this.indicator = indicator;
    }
}
