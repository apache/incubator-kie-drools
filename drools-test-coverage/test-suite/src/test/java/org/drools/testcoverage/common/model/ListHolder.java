package org.drools.testcoverage.common.model;

import java.io.Serializable;
import java.util.List;

public class ListHolder implements Serializable {

    private static final long serialVersionUID = -8093528616032514951L;
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
