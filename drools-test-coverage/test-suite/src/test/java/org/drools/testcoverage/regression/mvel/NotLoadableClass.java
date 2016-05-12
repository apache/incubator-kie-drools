package org.drools.testcoverage.regression.mvel;

import java.io.Serializable;

public class NotLoadableClass implements Serializable {

    private static final long serialVersionUID = -1534007611949004502L;

    static {
        // cause an ExceptionInInitializerError to simulate BZ 1321281
        int divisionByZero = 10 / 0;
    }
}
