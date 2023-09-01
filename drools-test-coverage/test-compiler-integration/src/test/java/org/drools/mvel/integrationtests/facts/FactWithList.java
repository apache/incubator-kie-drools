package org.drools.mvel.integrationtests.facts;

import java.util.ArrayList;
import java.util.List;

public class FactWithList {

    private List<String> items = new ArrayList<>();

    public FactWithList() { }

    public FactWithList(final String factString) {
        this.items.add(factString);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
