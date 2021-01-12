package java.time.format;

import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

public class DateTimeFormatter {

    public static final DateTimeFormatter ISO_DATE = null;
    public static final DateTimeFormatter ISO_LOCAL_TIME = null;

    public TemporalAccessor parse(CharSequence text) {
        return null;
    }

    public <T> T parse(CharSequence text, TemporalQuery<T> query) {
        return null;
    }

    public String format(TemporalAccessor temporal) {
        return null;
    }

    public TemporalAccessor parseBest(CharSequence text, TemporalQuery<?>... queries) {
        return null;
    }

    public DateTimeFormatter withResolverStyle(ResolverStyle resolverStyle) {
        return null;
    }
}