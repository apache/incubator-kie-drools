package org.optaplanner.benchmark.impl.statistic.common;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class MillisecondsSpentNumberFormat extends NumberFormat {

    private static final long DAY_MILLIS = 3600000L * 24L;
    private static final long HOUR_MILLIS = 3600000L;
    private static final long MINUTE_MILLIS = 60000L;
    private static final long SECOND_MILLIS = 1000L;

    private final Locale locale;

    public MillisecondsSpentNumberFormat(Locale locale) {
        this.locale = locale;
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return format((long) number, toAppendTo, pos);
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        if (number == 0L) {
            toAppendTo.append("0");
        }
        long rest = number;
        long days = rest / DAY_MILLIS;
        if (days > 0) {
            toAppendTo.append(days).append("d");
            rest %= DAY_MILLIS;
        }
        long hours = rest / HOUR_MILLIS;
        if (hours > 0) {
            toAppendTo.append(hours).append("h");
            rest %= HOUR_MILLIS;
        }
        long minutes = rest / MINUTE_MILLIS;
        if (minutes > 0) {
            toAppendTo.append(minutes).append("m");
            rest %= MINUTE_MILLIS;
        }
        long seconds = rest / SECOND_MILLIS;
        if (seconds > 0) {
            toAppendTo.append(seconds).append("s");
            rest %= SECOND_MILLIS;
        }
        if (rest > 0) {
            toAppendTo.append(rest).append("ms");
        }
        return toAppendTo;
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        throw new UnsupportedOperationException();
    }

}
