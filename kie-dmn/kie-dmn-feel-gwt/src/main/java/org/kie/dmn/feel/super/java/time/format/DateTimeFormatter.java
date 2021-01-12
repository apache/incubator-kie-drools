
package java.time.format;

import java.text.Format;
import java.text.ParsePosition;
import java.time.Period;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.util.Locale;
import java.util.Set;

public class DateTimeFormatter {

    public static final DateTimeFormatter ISO_LOCAL_DATE = null;
    public static final DateTimeFormatter ISO_OFFSET_DATE = null;
    public static final DateTimeFormatter ISO_DATE = null;
    public static final DateTimeFormatter ISO_LOCAL_TIME = null;
    public static final DateTimeFormatter ISO_OFFSET_TIME = null;
    public static final DateTimeFormatter ISO_TIME = null;
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME = null;
    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME = null;
    public static final DateTimeFormatter ISO_ZONED_DATE_TIME = null;
    public static final DateTimeFormatter ISO_DATE_TIME = null;
    public static final DateTimeFormatter ISO_ORDINAL_DATE = null;
    public static final DateTimeFormatter ISO_WEEK_DATE = null;
    public static final DateTimeFormatter ISO_INSTANT = null;
    public static final DateTimeFormatter BASIC_ISO_DATE = null;
    public static final DateTimeFormatter RFC_1123_DATE_TIME = null;

    public Locale getLocale() {
        return null;
    }

    public DateTimeFormatter withLocale(final Locale locale) {
        return null;
    }

    public DecimalStyle getDecimalStyle() {
        return null;
    }

    public DateTimeFormatter withDecimalStyle(final DecimalStyle decimalStyle) {
        return null;
    }

    public Chronology getChronology() {
        return null;
    }

    public DateTimeFormatter withChronology(final Chronology chrono) {
        return null;
    }

    public ZoneId getZone() {
        return null;
    }

    public DateTimeFormatter withZone(final ZoneId zone) {
        return null;
    }

    public ResolverStyle getResolverStyle() {
        return null;
    }

    public DateTimeFormatter withResolverStyle(final ResolverStyle resolverStyle) {
        return null;
    }

    public Set<TemporalField> getResolverFields() {
        return null;
    }

    public DateTimeFormatter withResolverFields(final TemporalField... resolverFields) {
        return null;
    }

    public DateTimeFormatter withResolverFields(final Set<TemporalField> resolverFields) {
        return null;
    }

    public String format(final TemporalAccessor temporal) {
        return null;
    }

    public void formatTo(final TemporalAccessor temporal, final Appendable appendable) {
    }

    public TemporalAccessor parse(final CharSequence text) {
        return null;
    }

    public TemporalAccessor parse(final CharSequence text, final ParsePosition position) {
        return null;
    }

    public <T> T parse(final CharSequence text, final TemporalQuery<T> query) {
        return null;
    }

    public TemporalAccessor parseBest(final CharSequence text, final TemporalQuery<?>... queries) {
        return null;
    }

    public TemporalAccessor parseUnresolved(final CharSequence text, final ParsePosition position) {
        return null;
    }

    public Format toFormat() {
        return null;
    }

    public Format toFormat(final TemporalQuery<?> parseQuery) {
        return null;
    }

    public String toString() {
        return null;
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return null;
    }

    public static DateTimeFormatter ofPattern(String pattern, Locale locale) {
        return null;
    }

    public static DateTimeFormatter ofLocalizedDate(FormatStyle dateStyle) {
        return null;
    }

    public static DateTimeFormatter ofLocalizedTime(FormatStyle timeStyle) {
        return null;
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateTimeStyle) {
        return null;
    }

    public static DateTimeFormatter ofLocalizedDateTime(FormatStyle dateStyle, FormatStyle timeStyle) {
        return null;
    }

    public static final TemporalQuery<Period> parsedExcessDays() {
        return null;
    }

    public static final TemporalQuery<Boolean> parsedLeapSecond() {
        return null;
    }
}
