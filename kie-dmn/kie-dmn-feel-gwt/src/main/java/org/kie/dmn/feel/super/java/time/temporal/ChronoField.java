package java.time.temporal;

public enum ChronoField
        implements TemporalField {
    DAY_OF_MONTH,
    MONTH_OF_YEAR,
    YEAR,
    HOUR_OF_DAY,
    MINUTE_OF_HOUR,
    SECOND_OF_MINUTE,
    OFFSET_SECONDS,
    DAY_OF_WEEK,
    DAY_OF_YEAR;

    public TemporalUnit getBaseUnit() {
        return null;
    }

    public TemporalUnit getRangeUnit() {
        return null;
    }

    public ValueRange range() {
        return null;
    }

    public boolean isDateBased() {
        return false;
    }

    public boolean isTimeBased() {
        return false;
    }

    public boolean isSupportedBy(final TemporalAccessor temporal) {
        return false;
    }

    public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
        return null;
    }

    public long getFrom(final TemporalAccessor temporal) {
        return 0;
    }

    public <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
        return null;
    }
}