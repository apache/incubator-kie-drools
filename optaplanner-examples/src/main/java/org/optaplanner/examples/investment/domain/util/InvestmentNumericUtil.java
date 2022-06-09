package org.optaplanner.examples.investment.domain.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InvestmentNumericUtil {

    public static final long MAXIMUM_QUANTITY_MILLIS = 1000L;

    public static final String MILLIS_NUMBER_PATTERN = "#0.000";
    public static final String MILLIS_PERCENT_PATTERN = "#0.0%";
    // TODO Remove potential multithreaded use of a thread-unsafe class
    protected static final NumberFormat MILLIS_NUMBER_FORMAT = new DecimalFormat(MILLIS_NUMBER_PATTERN);
    protected static final NumberFormat MILLIS_PERCENT_FORMAT = new DecimalFormat(MILLIS_PERCENT_PATTERN);

    protected static final BigDecimal MILLIS_DIVISOR = new BigDecimal(1000L);
    protected static final BigDecimal MICROS_DIVISOR = new BigDecimal(1000000L);

    public static String formatMillisAsNumber(long millis) {
        BigDecimal value = new BigDecimal(millis).divide(MILLIS_DIVISOR, 3, RoundingMode.HALF_UP);
        return MILLIS_NUMBER_FORMAT.format(value);
    }

    public static String formatMillisAsPercentage(long millis) {
        BigDecimal value = new BigDecimal(millis).divide(MILLIS_DIVISOR, 3, RoundingMode.HALF_UP);
        return MILLIS_PERCENT_FORMAT.format(value);
    }

    public static String formatMicrosAsPercentage(long micros) {
        BigDecimal value = new BigDecimal(micros).divide(MICROS_DIVISOR, 6, RoundingMode.HALF_UP);
        return MILLIS_PERCENT_FORMAT.format(value);
    }

    private InvestmentNumericUtil() {
    }

}
