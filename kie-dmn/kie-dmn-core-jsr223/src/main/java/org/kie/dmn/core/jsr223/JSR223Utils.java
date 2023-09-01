package org.kie.dmn.core.jsr223;

import java.math.BigDecimal;

public class JSR223Utils {
    
    private JSR223Utils() {
        // only static utils method.
    }
    
    public static double doubleValueExact(BigDecimal original) {
        double result = original.doubleValue();
        if (!(Double.isNaN(result) || Double.isInfinite(result))) {
            if (new BigDecimal(String.valueOf(result)).compareTo(original) == 0) {
                return result;
            }
        }
        throw new ArithmeticException(String.format("Conversion of %s incurred in loss of precision from BigDecimal", original));
    }

    /**
     * TODO PROVISIONAL, as this does not support non-latin characters, and without accents.
     */
    public static String escapeIdentifierForBinding(String original) {
        StringBuilder sb = new StringBuilder(original.length());
        Iterable<Integer> iterable = original.codePoints()::iterator;
        int i = 0;
        for (Integer cp : iterable) {
            if (i == 0) {
                if (cp >= '0' && cp <= '9') {
                    sb.append("_");
                }
            }
            if (cp >= '0' && cp <= '9') {
                sb.append((char) (int) cp);
            } else if (cp >= 'a' && cp <= 'z') {
                sb.append((char) (int) cp);
            } else if (cp >= 'A' && cp <= 'Z') {
                sb.append((char) (int) cp);
            } else {
                sb.append("_");
            }
            i++;
        }
        return sb.toString();
    }
}
