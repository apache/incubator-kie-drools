package org.optaplanner.quarkus.testdata.extended;

import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

public class TestdataExtendedQuarkusSolution extends TestdataQuarkusSolution {
    private String extraData;

    public TestdataExtendedQuarkusSolution() {
    }

    public TestdataExtendedQuarkusSolution(String extraData) {
        this.extraData = extraData;
    }

    public String getExtraData() {
        return extraData;
    }
}
