package org.drools.mvel;

import org.drools.mvel.util.MVELEvaluator;

public class MVELSafeHelper {

    private static class MVELEvaluatorHolder {

        private static final MVELEvaluator evaluator = new UnsafeMVELEvaluator();
    }

    private MVELSafeHelper() {
    }

    public static MVELEvaluator getEvaluator() {
        return MVELEvaluatorHolder.evaluator;
    }
}
