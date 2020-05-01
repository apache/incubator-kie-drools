package org.optaplanner.core.impl.testdata.domain.comparable;

import java.util.Comparator;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataCodeComparator implements Comparator<TestdataObject> {

    private static final Comparator<TestdataObject> COMPARATOR = Comparator.comparing(TestdataObject::getCode);

    public int compare(TestdataObject a, TestdataObject b) {
        return COMPARATOR.compare(a, b);
    }
}
