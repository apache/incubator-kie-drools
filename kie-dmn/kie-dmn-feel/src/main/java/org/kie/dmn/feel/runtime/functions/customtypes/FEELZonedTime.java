package org.kie.dmn.feel.runtime.functions.customtypes;

import org.kie.dmn.feel.util.Msg;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

public class FEELZonedTime implements Temporal {

    private final ZonedDateTime zonedDateTime;

    public static FEELZonedTime from(final TemporalAccessor temporal) {
        if (temporal instanceof FEELZonedTime) {
            return (FEELZonedTime) temporal;
        }
        try {
            final LocalTime time = LocalTime.from(temporal);
            final ZoneId zone = temporal.query(TemporalQueries.zone());
            if (zone == null) {
                throw new DateTimeException(Msg.createMessage(Msg.NO_ZONE_OR_OFFSET_IN_TEMPORAL, temporal));
            }
            return new FEELZonedTime(time, zone);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain ZonedTime from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static FEELZonedTime of(LocalTime time, ZoneId zoneId) {
        return new FEELZonedTime(time, zoneId);
    }

    public static FEELZonedTime of(int hour, int minute, int second, int nanoOfSecond, ZoneId zoneId) {
        return new FEELZonedTime(LocalTime.of(hour, minute, second, nanoOfSecond), zoneId);
    }

    public static FEELZonedTime now() {
        return new FEELZonedTime(ZonedDateTime.now());
    }

    public FEELZonedTime(final LocalTime time, final ZoneId zoneId) {
        Objects.requireNonNull(time);
        Objects.requireNonNull(zoneId);
        this.zonedDateTime = ZonedDateTime.of(LocalDate.now(), time, zoneId);
    }

    private FEELZonedTime(final ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return unit.isTimeBased();
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        if (isSupported(unit)) {
            return new FEELZonedTime(zonedDateTime.plus(amountToAdd, unit));
        } else {
            throw unsupportedTemporalUnitException();
        }
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        if (isSupported(unit)) {
            if (!endExclusive.isSupported(ChronoUnit.DAYS)) {
                return zonedDateTime.toOffsetDateTime().toOffsetTime().until(endExclusive, unit);
            } else {
                return zonedDateTime.until(endExclusive, unit);
            }
        } else {
            throw unsupportedTemporalUnitException();
        }
    }

    @Override
    public boolean isSupported(TemporalField field) {
        // If there is ZoneRegion set, offset seconds are not supported.
        if (field == ChronoField.OFFSET_SECONDS && !zonedDateTime.getZone().getClass().isAssignableFrom(ZoneOffset.class)) {
            return false;
        } else {
            return (field.isTimeBased() || field == ChronoField.OFFSET_SECONDS);
        }
    }

    @Override
    public long getLong(TemporalField field) {
        if (isSupported(field)) {
            return zonedDateTime.getLong(field);
        } else {
            throw unsupportedTemporalFieldException();
        }
    }

    @Override
    public Temporal with(TemporalAdjuster adjuster) {
        final ZonedDateTime wasZonedTemporalResult;
        if (adjuster instanceof TemporalAccessor) {
            wasZonedTemporalResult = withZonedTemporal((TemporalAccessor) adjuster);
            if (wasZonedTemporalResult == null) {
                return new FEELZonedTime(zonedDateTime.with(adjuster));
            } else {
                return new FEELZonedTime(wasZonedTemporalResult);
            }
        } else {
            return new FEELZonedTime(zonedDateTime.with(adjuster));
        }
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        if (isSupported(field)) {
            if (field == ChronoField.OFFSET_SECONDS) {
                return new FEELZonedTime(zonedDateTime.withZoneSameLocal(ZoneOffset.ofTotalSeconds(Math.toIntExact(newValue))));
            } else {
                return new FEELZonedTime(zonedDateTime.with(field, newValue));
            }
        } else {
            throw unsupportedTemporalFieldException();
        }
    }

    @Override
    public Temporal plus(TemporalAmount amount) {
        return new FEELZonedTime(zonedDateTime.plus(amount));
    }

    @Override
    public Temporal minus(TemporalAmount amount) {
        return new FEELZonedTime(zonedDateTime.minus(amount));
    }

    @Override
    public Temporal minus(long amountToSubtract, TemporalUnit unit) {
        if (isSupported(unit)) {
            return new FEELZonedTime(zonedDateTime.minus(amountToSubtract, unit));
        } else {
            throw unsupportedTemporalUnitException();
        }
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (isSupported(field)) {
            return zonedDateTime.range(field);
        } else {
            throw unsupportedTemporalFieldException();
        }
    }

    @Override
    public int get(TemporalField field) {
        if (isSupported(field)) {
            return zonedDateTime.get(field);
        } else {
            throw unsupportedTemporalFieldException();
        }
    }

    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.localDate() || query == TemporalQueries.chronology()) {
            return null;
        } else {
            return zonedDateTime.query(query);
        }
    }

    private DateTimeException unsupportedTemporalUnitException() {
        return new DateTimeException(Msg.createMessage(Msg.ZONED_TIME_UNSUPPORTED_TEMPORAL_UNIT));
    }

    private DateTimeException unsupportedTemporalFieldException() {
        return new DateTimeException(Msg.createMessage(Msg.ZONED_TIME_UNSUPPORTED_TEMPORAL_FIELD));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FEELZonedTime) {
            FEELZonedTime other = (FEELZonedTime) obj;
            final boolean timeEquals = zonedDateTime.toLocalTime().equals(other.zonedDateTime.toLocalTime());
            return timeEquals
                    && zonesEqual(zonedDateTime.getZone(), other.zonedDateTime.getZone())
                    && zonesEqual(zonedDateTime.getOffset(), other.zonedDateTime.getOffset());
        }
        return false;
    }

    private boolean zonesEqual(final ZoneId first, final ZoneId second) {
        return (first == null && second == null) || (first != null && first.equals(second));
    }

    @Override
    public int hashCode() {
        if (zonedDateTime.getZone() != null) {
            return zonedDateTime.toLocalTime().hashCode() ^ zonedDateTime.getZone().hashCode();
        } else {
            return zonedDateTime.toLocalTime().hashCode() ^ zonedDateTime.getOffset().hashCode();
        }
    }

    @Override
    public String toString() {
        // If the zone is ZoneRegion, don't add offset. This is a specific of time string in FEEL.
        if (!(zonedDateTime.getZone() instanceof ZoneOffset)) {
            return zonedDateTime.toLocalTime().toString() + "@" + zonedDateTime.getZone();
        } else {
            return zonedDateTime.toLocalTime().toString() + zonedDateTime.getOffset().toString();
        }
    }

    private ZonedDateTime withZonedTemporal(final TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof OffsetTime
                || temporalAccessor instanceof OffsetDateTime
                || temporalAccessor instanceof FEELZonedTime
                || temporalAccessor instanceof ZonedDateTime) {
            final ZoneId zoneId = temporalAccessor.query(TemporalQueries.zone());
            final ZonedDateTime firstAdjustment = zonedDateTime.withZoneSameLocal(zoneId);
            return firstAdjustment.with(LocalTime.from(temporalAccessor));
        } else {
            return null;
        }
    }
}
