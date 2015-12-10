package org.kie.test.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.test.util.compare.ComparePair;

public class ComparePairTest {

    @Test
    public void collectionsTest() {
        List<String> orig = new ArrayList<String>();
        orig.add("asdfasdfasdfasdf");

        List<String> copy = new ArrayList<String>();
        copy.add(new String(orig.get(0)));

        ComparePair.compareObjectsViaFields(orig, copy);
    }
}
