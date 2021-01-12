
package java.time.format;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalField;

public final class DateTimeFormatterBuilder {

    public DateTimeFormatterBuilder parseCaseSensitive() {
        return null;
    }

    public DateTimeFormatterBuilder parseCaseInsensitive() {
        return null;
    }

    public DateTimeFormatterBuilder parseStrict() {
        return null;
    }

    public DateTimeFormatterBuilder parseLenient() {
        return null;
    }

    public DateTimeFormatterBuilder parseDefaulting(final TemporalField field, final long value) {
        return null;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field) {
        return null;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field, final int width) {
        return null;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field, final int minWidth, final int maxWidth, final SignStyle signStyle) {
        return null;
    }

    public DateTimeFormatterBuilder appendValueReduced(final TemporalField field, final int width, final int maxWidth, final int baseValue) {
        return null;
    }

    public DateTimeFormatterBuilder appendValueReduced(final TemporalField field, final int width, final int maxWidth, final ChronoLocalDate baseDate) {
        return null;
    }

    public DateTimeFormatterBuilder appendFraction(final TemporalField field, final int minWidth, final int maxWidth, final boolean decimalPoint) {
        return null;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field) {
        return null;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field, final TextStyle textStyle) {
        return null;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field, final Map<Long, String> textLookup) {
        return null;
    }

    public DateTimeFormatterBuilder appendInstant() {
        return null;
    }

    public DateTimeFormatterBuilder appendInstant(final int fractionalDigits) {
        return null;
    }

    public DateTimeFormatterBuilder appendOffsetId() {
        return null;
    }

    public DateTimeFormatterBuilder appendOffset(final String pattern, final String noOffsetText) {
        return null;
    }

    public DateTimeFormatterBuilder appendLocalizedOffset(final TextStyle style) {
        return null;
    }

    public DateTimeFormatterBuilder appendZoneId() {
        return null;
    }

    public DateTimeFormatterBuilder appendZoneRegionId() {
        return null;
    }

    public DateTimeFormatterBuilder appendZoneOrOffsetId() {
        return null;
    }

    public DateTimeFormatterBuilder appendZoneText(final TextStyle textStyle) {
        return null;
    }

    public DateTimeFormatterBuilder appendZoneText(final TextStyle textStyle, final Set<ZoneId> preferredZones) {
        return null;
    }

    public DateTimeFormatterBuilder appendChronologyId() {
        return null;
    }

    public DateTimeFormatterBuilder appendChronologyText(final TextStyle textStyle) {
        return null;
    }

    public DateTimeFormatterBuilder appendLocalized(final FormatStyle dateStyle, final FormatStyle timeStyle) {
        return null;
    }

    public DateTimeFormatterBuilder appendLiteral(final char literal) {
        return null;
    }

    public DateTimeFormatterBuilder appendLiteral(final String literal) {
        return null;
    }

    public DateTimeFormatterBuilder append(final DateTimeFormatter formatter) {
        return null;
    }

    public DateTimeFormatterBuilder appendOptional(final DateTimeFormatter formatter) {
        return null;
    }

    public DateTimeFormatterBuilder appendPattern(final String pattern) {
        return null;
    }

    public DateTimeFormatterBuilder padNext(final int padWidth) {
        return null;
    }

    public DateTimeFormatterBuilder padNext(final int padWidth, final char padChar) {
        return null;
    }

    public DateTimeFormatterBuilder optionalStart() {
        return null;
    }

    public DateTimeFormatterBuilder optionalEnd() {
        return null;
    }

    public DateTimeFormatter toFormatter() {
        return null;
    }

    public DateTimeFormatter toFormatter(final Locale locale) {
        return null;
    }
}
