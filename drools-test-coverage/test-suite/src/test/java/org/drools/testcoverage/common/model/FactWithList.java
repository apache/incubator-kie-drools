package org.drools.testcoverage.common.model;

import java.util.ArrayList;
import java.util.List;

public class FactWithList {

    private List<String> items = new ArrayList<>();

    public FactWithList(final String factString) {
        this.items.add(factString);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(final List<String> items) {
        this.items = items;
    }
}
