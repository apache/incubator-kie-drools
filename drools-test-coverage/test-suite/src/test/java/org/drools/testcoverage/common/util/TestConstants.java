package org.drools.testcoverage.common.util;

/**
 * Various constants used in tests.
 */
public final class TestConstants {

    public static final String PACKAGE_TESTCOVERAGE = "org.drools.testcoverage";
    public static final String PACKAGE_TESTCOVERAGE_MODEL = "org.drools.testcoverage.common.model";
    public static final String PACKAGE_REGRESSION = PACKAGE_TESTCOVERAGE + ".regression";
    public static final String PACKAGE_FUNCTIONAL = PACKAGE_TESTCOVERAGE + ".functional";

    public static final String TEST_RESOURCES_FOLDER = "src/main/resources/";
    public static final String DRL_TEST_TARGET_PATH = TEST_RESOURCES_FOLDER + "rule.drl";


    private TestConstants() {
        // Creating instances of util classes should not be possible.
    }
}
