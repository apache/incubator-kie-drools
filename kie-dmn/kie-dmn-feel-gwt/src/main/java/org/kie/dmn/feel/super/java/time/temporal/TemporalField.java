
package java.time.temporal;

import java.util.Locale;
import java.util.Map;

import java.time.format.ResolverStyle;

public interface TemporalField {

    default String getDisplayName(Locale locale) {
        return null;
    }

    TemporalUnit getBaseUnit();

    TemporalUnit getRangeUnit();

    ValueRange range();

    boolean isDateBased();

    boolean isTimeBased();

    boolean isSupportedBy(TemporalAccessor temporal);

    ValueRange rangeRefinedBy(TemporalAccessor temporal);

    long getFrom(TemporalAccessor temporal);

    <R extends Temporal> R adjustInto(R temporal, long newValue);

    default TemporalAccessor resolve(Map<java.time.temporal.TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
        return null;
    }

    String toString();
}
