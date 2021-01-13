
package java.time.format;

import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalField;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DateTimeFormatterBuilder {

    public DateTimeFormatterBuilder parseCaseSensitive() {
        return this;
    }

    public DateTimeFormatterBuilder parseCaseInsensitive() {
        return this;
    }

    public DateTimeFormatterBuilder parseStrict() {
        return this;
    }

    public DateTimeFormatterBuilder parseLenient() {
        return this;
    }

    public DateTimeFormatterBuilder parseDefaulting(final TemporalField field, final long value) {
        return this;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field) {
        return this;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field, final int width) {
        return this;
    }

    public DateTimeFormatterBuilder appendValue(final TemporalField field, final int minWidth, final int maxWidth, final SignStyle signStyle) {
        return this;
    }

    public DateTimeFormatterBuilder appendValueReduced(final TemporalField field, final int width, final int maxWidth, final int baseValue) {
        return this;
    }

    public DateTimeFormatterBuilder appendValueReduced(final TemporalField field, final int width, final int maxWidth, final ChronoLocalDate baseDate) {
        return this;
    }

    public DateTimeFormatterBuilder appendFraction(final TemporalField field, final int minWidth, final int maxWidth, final boolean decimalPoint) {
        return this;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field) {
        return this;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field, final TextStyle textStyle) {
        return this;
    }

    public DateTimeFormatterBuilder appendText(final TemporalField field, final Map<Long, String> textLookup) {
        return this;
    }

    public DateTimeFormatterBuilder appendInstant() {
        return this;
    }

    public DateTimeFormatterBuilder appendInstant(final int fractionalDigits) {
        return this;
    }

    public DateTimeFormatterBuilder appendOffsetId() {
        return this;
    }

    public DateTimeFormatterBuilder appendOffset(final String pattern, final String noOffsetText) {
        return this;
    }

    public DateTimeFormatterBuilder appendLocalizedOffset(final TextStyle style) {
        return this;
    }

    public DateTimeFormatterBuilder appendZoneId() {
        return this;
    }

    public DateTimeFormatterBuilder appendZoneRegionId() {
        return this;
    }

    public DateTimeFormatterBuilder appendZoneOrOffsetId() {
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(final TextStyle textStyle) {
        return this;
    }

    public DateTimeFormatterBuilder appendZoneText(final TextStyle textStyle, final Set<ZoneId> preferredZones) {
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyId() {
        return this;
    }

    public DateTimeFormatterBuilder appendChronologyText(final TextStyle textStyle) {
        return this;
    }

    public DateTimeFormatterBuilder appendLocalized(final FormatStyle dateStyle, final FormatStyle timeStyle) {
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(final char literal) {
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(final String literal) {
        return this;
    }

    public DateTimeFormatterBuilder append(final DateTimeFormatter formatter) {
        return this;
    }

    public DateTimeFormatterBuilder appendOptional(final DateTimeFormatter formatter) {
        return this;
    }

    public DateTimeFormatterBuilder appendPattern(final String pattern) {
        return this;
    }

    public DateTimeFormatterBuilder padNext(final int padWidth) {
        return this;
    }

    public DateTimeFormatterBuilder padNext(final int padWidth, final char padChar) {
        return this;
    }

    public DateTimeFormatterBuilder optionalStart() {
        return this;
    }

    public DateTimeFormatterBuilder optionalEnd() {
        return this;
    }

    public DateTimeFormatter toFormatter() {
        return new DateTimeFormatter();
    }

    public DateTimeFormatter toFormatter(final Locale locale) {
        return new DateTimeFormatter();
    }
}
