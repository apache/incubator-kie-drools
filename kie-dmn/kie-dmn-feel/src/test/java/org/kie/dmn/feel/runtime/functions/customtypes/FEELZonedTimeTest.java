package org.kie.dmn.feel.runtime.functions.customtypes;

import org.junit.Before;
import org.junit.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FEELZonedTimeTest {

    private LocalTime localTime;
    private ZoneId zoneRegion;
    private ZoneOffset zoneOffset;

    @Before
    public void setUp() throws Exception {
        localTime = LocalTime.of(14, 34, 23, 24);
        zoneRegion = ZoneId.of("Europe/Prague");
        zoneOffset = ZoneOffset.of("+04:00");
    }

    @Test
    public void fromNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.from(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void fromFEELZonedTimeInstance() {
        final FEELZonedTime time = new FEELZonedTime(LocalTime.now(), ZoneOffset.UTC);
        assertThat(FEELZonedTime.from(time)).isSameAs(time);
    }

    @Test
    public void fromLocalTime() {
        assertThatThrownBy(() -> FEELZonedTime.from(LocalTime.now())).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void fromMonth() {
        assertThatThrownBy(() -> FEELZonedTime.from(Month.FEBRUARY)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void fromZoneRegion() {
        final LocalDate localDate = LocalDate.of(2023, 4, 4);
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, zoneRegion);
        final FEELZonedTime zonedTime = FEELZonedTime.from(zonedDateTime);
        assertFromZoneRegion(zonedTime, localTime, zoneRegion);
    }

    @Test
    public void fromOffsetTime() {
        final OffsetTime offsetTime = OffsetTime.of(localTime, zoneOffset);
        final FEELZonedTime zonedTime = FEELZonedTime.from(offsetTime);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void ofNullParameters() {
        assertThatThrownBy(() -> FEELZonedTime.of(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void ofNullLocalTime() {
        assertThatThrownBy(() -> FEELZonedTime.of(null, ZoneOffset.UTC)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void ofNullZone() {
        assertThatThrownBy(() -> FEELZonedTime.of(LocalTime.now(), null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void ofLocalTimeAndZoneRegion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertFromZoneRegion(zonedTime, localTime, zoneRegion);
    }

    @Test
    public void ofLocalTimeAndZoneOffset() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void ofNullZone5ParameterVersion() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, 0, 0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void ofHourOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(-10, 0, 0, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void ofMinutesOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, -10, 0, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void ofSecondsOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, -10, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void ofNanosecondsOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, 0, -10, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void ofZoneRegion5ParameterVersion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano(), zoneRegion);
        assertFromZoneRegion(zonedTime, localTime, zoneRegion);
    }

    @Test
    public void ofZoneOffset5ParameterVersion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano(), zoneOffset);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void now() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        // This cannot be nondeterministic if comparing with a specific time, because we don't know what is the current time up front.
        // So this asserts only that the fields are not null and within required bounds.
        // Even asserting bounds like this is nondeterministic. We cannot enforce the range boundaries, but at least some asserts.
        assertThat(zonedTime).isNotNull();
        assertThat(zonedTime.get(ChronoField.HOUR_OF_DAY)).isBetween(0, 23);
        assertThat(zonedTime.get(ChronoField.MINUTE_OF_HOUR)).isBetween(0, 59);
        assertThat(zonedTime.get(ChronoField.SECOND_OF_MINUTE)).isBetween(0, 59);
        assertThat(zonedTime.get(ChronoField.NANO_OF_SECOND)).isBetween(0, 999999999);
    }

    @Test
    public void isSupportedTemporalUnit() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        for (ChronoUnit chronoUnit : ChronoUnit.values()) {
            if (chronoUnit.isTimeBased()) {
                assertThat(zonedTime.isSupported(chronoUnit)).isTrue();
            } else {
                assertThat(zonedTime.isSupported(chronoUnit)).isFalse();
            }
        }
    }

    @Test
    public void isSupportedTemporalFieldWhenContainsZoneRegion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertThat(zonedTime.isSupported(ChronoField.OFFSET_SECONDS)).isFalse();
        for (ChronoField chronoField : ChronoField.values()) {
            if (chronoField != ChronoField.OFFSET_SECONDS) {
                if (chronoField.isTimeBased()) {
                    assertThat(zonedTime.isSupported(chronoField)).isTrue();
                } else {
                    assertThat(zonedTime.isSupported(chronoField)).isFalse();
                }
            }
        }
    }

    @Test
    public void isSupportedTemporalFieldWhenContainsZoneOffset() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField : ChronoField.values()) {
            if (chronoField.isTimeBased() || chronoField == ChronoField.OFFSET_SECONDS) {
                assertThat(zonedTime.isSupported(chronoField)).as("Failed for ChronoField " + chronoField).isTrue();
            } else {
                assertThat(zonedTime.isSupported(chronoField)).as("Failed for ChronoField " + chronoField).isFalse();
            }
        }
    }

    @Test
    public void withWithNullAdjuster() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        assertThatThrownBy(() -> zonedTime.with(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void withWithAdjusterLocalDate() {
        Temporal feelZonedTime = new FEELZonedTime(localTime, zoneOffset);
        feelZonedTime = feelZonedTime.with(LocalDate.now());
        assertThat(feelZonedTime).isInstanceOf(FEELZonedTime.class);
        assertFromZoneOffset((FEELZonedTime) feelZonedTime, localTime, zoneOffset);
    }

    @Test
    public void withWithTimeAdjuster() {
        final ZoneOffset newOffset = ZoneOffset.of("+03:00");
        final OffsetTime expectedTime = OffsetTime.of(2, 4, 5, 7, newOffset);
        Temporal feelZonedTime = new FEELZonedTime(localTime, zoneOffset);
        feelZonedTime = feelZonedTime.with(expectedTime);
        assertThat(feelZonedTime).isInstanceOf(FEELZonedTime.class);

        assertFromZoneOffset((FEELZonedTime) feelZonedTime, LocalTime.from(expectedTime), newOffset);
    }

    @Test
    public void withWithTemporalFieldNull() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        assertThatThrownBy(() -> zonedTime.with(null, 0)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void withWithTemporalFieldUnsupportedOnZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoField chronoField : ChronoField.values()) {
            if (!chronoField.isTimeBased()) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, 0), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void withWithTemporalFieldUnsupportedOnZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField : ChronoField.values()) {
            if (!chronoField.isTimeBased() && !(chronoField == ChronoField.OFFSET_SECONDS)) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, 0), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void withWithTemporalFieldValueOutOfBounds() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        for (ChronoField chronoField : ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, -12), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void withWithZoneRegionTimeTemporalFieldVersion() {
        // It is not possible to change zone region to offset zone, so using the same zone.
        final FEELZonedTime expectedTime = FEELZonedTime.of(LocalTime.of(2, 4, 5, 7), zoneRegion);
        Temporal zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        zonedTime = applyWithOnTimeUsingTemporalFields(zonedTime, expectedTime);
        assertThat(zonedTime).isInstanceOf(FEELZonedTime.class);
        assertFromZoneRegion((FEELZonedTime) zonedTime, LocalTime.from(expectedTime), ZoneId.from(expectedTime));
    }

    @Test
    public void withWithZoneOffsetTimeTemporalFieldVersion() {
        // It is not possible to change zone region to offset zone, so using the same zone.
        final ZoneOffset newOffset = ZoneOffset.of("+03:00");
        final OffsetTime expectedTime = OffsetTime.of(2, 4, 5, 7, newOffset);
        Temporal zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        zonedTime = applyWithOnTimeUsingTemporalFields(zonedTime, expectedTime);
        zonedTime = zonedTime.with(ChronoField.OFFSET_SECONDS, newOffset.getTotalSeconds());
        assertThat(zonedTime).isInstanceOf(FEELZonedTime.class);
        assertFromZoneOffset((FEELZonedTime) zonedTime, LocalTime.from(expectedTime), newOffset);
    }

    private Temporal applyWithOnTimeUsingTemporalFields(final Temporal timeTemporal, final TemporalAccessor temporalAccessor) {
        Temporal updatedTemporal = timeTemporal.with(ChronoField.HOUR_OF_DAY, temporalAccessor.get(ChronoField.HOUR_OF_DAY));
        updatedTemporal = updatedTemporal.with(ChronoField.MINUTE_OF_HOUR, temporalAccessor.get(ChronoField.MINUTE_OF_HOUR));
        updatedTemporal = updatedTemporal.with(ChronoField.SECOND_OF_MINUTE, temporalAccessor.get(ChronoField.SECOND_OF_MINUTE));
        return updatedTemporal.with(ChronoField.NANO_OF_SECOND, temporalAccessor.get(ChronoField.NANO_OF_SECOND));
    }

    @Test
    public void plus() {
        // TODO
    }

    @Test
    public void until() {
        // TODO
    }

    @Test
    public void testIsSupported() {
        // TODO
    }

    @Test
    public void getLong() {
        // TODO
    }

    @Test
    public void testWith() {
        // TODO
    }

    @Test
    public void testPlus() {
        // TODO
    }

    @Test
    public void minus() {
        // TODO
    }

    @Test
    public void testMinus() {
        // TODO
    }

    @Test
    public void range() {
        // TODO
    }

    @Test
    public void get() {
        // TODO
    }

    @Test
    public void query() {
        // TODO
    }

    @Test
    public void testEquals() {
        // TODO
    }

    @Test
    public void testHashCode() {
        // TODO
    }

    @Test
    public void testToString() {
        // TODO
    }

    private void assertFromZoneRegion(final FEELZonedTime zonedTime, final LocalTime expectedTime, final ZoneId expectedZoneId) {
        assertLocalTime(zonedTime, expectedTime);
        assertThatThrownBy(() -> zonedTime.get(ChronoField.OFFSET_SECONDS)).isInstanceOf(DateTimeException.class);
        assertThat(zonedTime.query(TemporalQueries.zone())).isEqualTo(expectedZoneId);
    }

    private void assertFromZoneOffset(final FEELZonedTime zonedTime, final LocalTime expectedTime, final ZoneOffset expectedZoneOffset) {
        assertLocalTime(zonedTime, expectedTime);
        assertThat(zonedTime.query(TemporalQueries.zone())).isEqualTo(expectedZoneOffset);
        assertThat(zonedTime.get(ChronoField.OFFSET_SECONDS)).isEqualTo(expectedZoneOffset.getTotalSeconds());
    }

    private void assertLocalTime(final FEELZonedTime zonedTime, final LocalTime expectedLocalTime) {
        assertThat(zonedTime).isNotNull();
        assertThat(zonedTime.get(ChronoField.HOUR_OF_DAY)).isEqualTo(expectedLocalTime.getHour());
        assertThat(zonedTime.get(ChronoField.MINUTE_OF_HOUR)).isEqualTo(expectedLocalTime.getMinute());
        assertThat(zonedTime.get(ChronoField.SECOND_OF_MINUTE)).isEqualTo(expectedLocalTime.getSecond());
        assertThat(zonedTime.get(ChronoField.NANO_OF_SECOND)).isEqualTo(expectedLocalTime.getNano());
    }
}