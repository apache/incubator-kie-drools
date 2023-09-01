package org.drools.impact.analysis.integrationtests.domain;

import java.math.BigDecimal;
import java.util.Map;

public class FunctionUtils {

    public static BigDecimal convertMapToBigDecimal(Map<String, String> map, String key) {
        String str = map.get(key);
        return new BigDecimal(str);
    }
}
