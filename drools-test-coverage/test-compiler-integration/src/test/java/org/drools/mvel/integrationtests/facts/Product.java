package org.drools.mvel.integrationtests.facts;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String id;
    private final String category;

    private List<String> firings = new ArrayList<>();

    private String description = "";

    public Product(final String id, final String category ) {
        this.id = id;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public List<String> getFirings() {
        return firings;
    }

    public String getCategory() {
        return category;
    }

    public CategoryTypeEnum getCategoryAsEnum() {
        return CategoryTypeEnum.fromString(category);
    }

    public String getDescription() {
        return description;
    }

    public void appendDescription( final String append ) {
        description += append;
    }
}
