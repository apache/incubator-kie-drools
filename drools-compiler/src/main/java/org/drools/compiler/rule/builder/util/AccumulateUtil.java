package org.drools.compiler.rule.builder.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AccumulateUtil {

    public static String getFunctionName(Class<?> exprClass, String functionName) {
        if (functionName.equals("sum" )) {
            if (exprClass == int.class || exprClass == Integer.class) {
                functionName = "sumI";
            } else if (exprClass == long.class || exprClass == Long.class) {
                functionName = "sumL";
            } else if (exprClass == BigInteger.class) {
                functionName = "sumBI";
            } else if (exprClass == BigDecimal.class) {
                functionName = "sumBD";
            }
        } else if (functionName.equals("average" )) {
            if (exprClass == BigDecimal.class) {
                functionName = "averageBD";
            }
        }
        return functionName;
    }
}
