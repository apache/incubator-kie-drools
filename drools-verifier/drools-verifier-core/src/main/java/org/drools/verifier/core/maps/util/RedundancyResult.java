package org.drools.verifier.core.maps.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.relations.IsRedundant;

public class RedundancyResult<Parent, Item extends IsRedundant & HumanReadable> {

    public static final RedundancyResult EMPTY = new RedundancyResult();

    protected final List<Item> list = new ArrayList<>();
    private Parent parent;

    public RedundancyResult(final Item... items) {
        Collections.addAll(list, items);
    }

    public RedundancyResult(final Parent parent,
                            final RedundancyResult<Object, Item> result) {
        this.parent = parent;
        list.addAll(result.list);
    }

    public boolean isTrue() {
        return !list.isEmpty();
    }

    public Item get(final int i) {
        return list.get(i);
    }

    public Parent getParent() {
        return parent;
    }
}
