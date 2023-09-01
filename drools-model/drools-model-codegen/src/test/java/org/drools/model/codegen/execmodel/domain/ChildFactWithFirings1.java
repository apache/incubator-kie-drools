package org.drools.model.codegen.execmodel.domain;

import java.util.ArrayList;
import java.util.List;

public class ChildFactWithFirings1 {

    private final int id;
    private final int parentId;
    private final List<String> firings;

    private String evaluationName;

    public ChildFactWithFirings1(final int id, final int parentId) {
        this.id = id;
        this.parentId = parentId;
        this.firings = new ArrayList<>();
        this.evaluationName = "";
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public List<String> getFirings() {
        return firings;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(final String evaluationName) {
        this.evaluationName = evaluationName;
    }
}
