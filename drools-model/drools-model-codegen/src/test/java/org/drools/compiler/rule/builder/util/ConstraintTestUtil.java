package org.drools.compiler.rule.builder.util;

public class ConstraintTestUtil {

    // for test only
    public static void disableNormalizeConstraint() {
        ConstraintUtil.ENABLE_NORMALIZE = false;
    }

    // for test only
    public static void enableNormalizeConstraint() {
        ConstraintUtil.ENABLE_NORMALIZE = true;
    }
}
