package org.kie.dmn.feel.runtime.functions.customtypes;

import org.junit.Before;
import org.junit.Test;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
    public void testFromMethodWithNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.from(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testFromMethodWithFEELZonedTimeInstance() {
        final FEELZonedTime time = new FEELZonedTime(LocalTime.now(), ZoneOffset.UTC);
        assertThat(FEELZonedTime.from(time)).isSameAs(time);
    }

    @Test
    public void testFromMethodWithLocalTime() {
        assertThatThrownBy(() -> FEELZonedTime.from(LocalTime.now())).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testFromMethodWithMonth() {
        assertThatThrownBy(() -> FEELZonedTime.from(Month.FEBRUARY)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testFromMethodWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        final FEELZonedTime newZonedTime = FEELZonedTime.from(zonedTime);
        assertFromZoneRegion(newZonedTime, localTime, zoneRegion);
    }

    @Test
    public void testFromMethodWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        final FEELZonedTime newZonedTime = FEELZonedTime.from(zonedTime);
        assertFromZoneOffset(newZonedTime, localTime, zoneOffset);
    }

    @Test
    public void testFromMethodWithOffsetTime() {
        final OffsetTime offsetTime = OffsetTime.of(localTime, zoneOffset);
        final FEELZonedTime zonedTime = FEELZonedTime.from(offsetTime);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void testOfMethodWithNullParameters() {
        assertThatThrownBy(() -> FEELZonedTime.of(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testOfMethodWithNullLocalTime() {
        assertThatThrownBy(() -> FEELZonedTime.of(null, ZoneOffset.UTC)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testOfMethodWithNullZone() {
        assertThatThrownBy(() -> FEELZonedTime.of(LocalTime.now(), null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testOfMethodWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertFromZoneRegion(zonedTime, localTime, zoneRegion);
    }

    @Test
    public void testOfMethodWithLocalTimeAndZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void testOfMethodWithNullZone5ParameterVersion() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, 0, 0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testOfMethodWithHourOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(-10, 0, 0, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testOfMethodWithMinutesOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, -10, 0, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testOfMethodWithSecondsOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, -10, 0, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testOfMethodWithNanosecondsOutOfRange() {
        assertThatThrownBy(() -> FEELZonedTime.of(0, 0, 0, -10, ZoneOffset.UTC)).isInstanceOf(DateTimeException.class);
    }

    @Test
    public void testOfMethodWithZoneRegionTime5ParameterVersion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano(), zoneRegion);
        assertFromZoneRegion(zonedTime, localTime, zoneRegion);
    }

    @Test
    public void testOfMethodWithZoneOffsetTime5ParameterVersion() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano(), zoneOffset);
        assertFromZoneOffset(zonedTime, localTime, zoneOffset);
    }

    @Test
    public void testNowMethod() {
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
    public void testIsSupportedMethodWithTemporalUnit() {
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
    public void testIsSupportedMethodWithTemporalFieldWhenContainsZoneRegion() {
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
    public void testIsSupportedMethodWithTemporalFieldWhenContainsZoneOffset() {
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
    public void testWithMethodWithNullAdjuster() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        assertThatThrownBy(() -> zonedTime.with(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testWithMethodWithAdjusterLocalDate() {
        Temporal feelZonedTime = new FEELZonedTime(localTime, zoneOffset);
        feelZonedTime = feelZonedTime.with(LocalDate.now());
        assertThat(feelZonedTime).isInstanceOf(FEELZonedTime.class);
        assertFromZoneOffset((FEELZonedTime) feelZonedTime, localTime, zoneOffset);
    }

    @Test
    public void testWithMethodWithTimeAdjuster() {
        final ZoneOffset newOffset = ZoneOffset.of("+03:00");
        final OffsetTime expectedTime = OffsetTime.of(2, 4, 5, 7, newOffset);
        Temporal feelZonedTime = new FEELZonedTime(localTime, zoneOffset);
        feelZonedTime = feelZonedTime.with(expectedTime);
        assertThat(feelZonedTime).isInstanceOf(FEELZonedTime.class);

        assertFromZoneOffset((FEELZonedTime) feelZonedTime, LocalTime.from(expectedTime), newOffset);
    }

    @Test
    public void testWithMethodWithTemporalFieldNull() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        assertThatThrownBy(() -> zonedTime.with(null, 0)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testWithMethodWithTemporalFieldUnsupportedOnZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoField chronoField : ChronoField.values()) {
            if (!chronoField.isTimeBased()) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, 0), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testWithMethodWithTemporalFieldUnsupportedOnZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField : ChronoField.values()) {
            if (!chronoField.isTimeBased() && !(chronoField == ChronoField.OFFSET_SECONDS)) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, 0), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testWithMethodWithTemporalFieldValueOutOfBounds() {
        final FEELZonedTime zonedTime = FEELZonedTime.now();
        for (ChronoField chronoField : ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThatThrownBy(() -> zonedTime.with(chronoField, -12), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testWithMethodWithZoneRegionTimeTemporalFieldVersion() {
        // It is not possible to change zone region to offset zone, so using the same zone.
        final FEELZonedTime expectedTime = FEELZonedTime.of(LocalTime.of(2, 4, 5, 7), zoneRegion);
        Temporal zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        zonedTime = applyWithOnTimeUsingTemporalFields(zonedTime, expectedTime);
        assertThat(zonedTime).isInstanceOf(FEELZonedTime.class);
        assertFromZoneRegion((FEELZonedTime) zonedTime, LocalTime.from(expectedTime), ZoneId.from(expectedTime));
    }

    @Test
    public void testWithMethodWithZoneOffsetTimeTemporalFieldVersion() {
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
    public void testPlusMethodWithTemporalAmountNull() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).plus(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testPlusMethodWithDuration() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        final int addedHours = 31;
        final Duration duration = Duration.ofHours(addedHours);
        final Temporal updatedTime = zonedTime.plus(duration);
        assertThat(updatedTime).isInstanceOf(FEELZonedTime.class);
        final Temporal expectedTime = zonedTime.with(ChronoField.HOUR_OF_DAY, localTime.getHour() + (addedHours - 24));
        assertThat(updatedTime).isEqualTo(expectedTime);
    }

    @Test
    public void testPlusMethodWithPeriod() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        final Period period = Period.ofMonths(3);
        final Temporal updatedTime = zonedTime.plus(period);
        assertThat(updatedTime).isInstanceOf(FEELZonedTime.class);
        assertThat(updatedTime).isEqualTo(zonedTime);
    }

    @Test
    public void testPlusMethodWithChronoUnitNull() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).plus(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testPlusMethodWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        testPlusMethodWithChronoUnitParams(zonedTime, localTime, 2, zoneRegion);
    }

    @Test
    public void testPlusMethodWithZoneRegionTimeNegativeAmountToAdd() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        testPlusMethodWithChronoUnitParams(zonedTime, localTime, -2, zoneRegion);
    }

    @Test
    public void testPlusMethodWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        testPlusMethodWithChronoUnitParams(zonedTime, localTime, 2, zoneOffset);
    }

    @Test
    public void testPlusMethodWithZoneOffsetTimeNegativeAmountToAdd() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        testPlusMethodWithChronoUnitParams(zonedTime, localTime, -2, zoneOffset);
    }

    private void testPlusMethodWithChronoUnitParams(final FEELZonedTime time, final LocalTime baseForExpectedTime, final long amountToAdd, final ZoneId timeZoneId) {
        for (ChronoUnit chronoUnit: ChronoUnit.values()) {
            if (chronoUnit.isTimeBased()) {
                final FEELZonedTime expectedTime = FEELZonedTime.of(baseForExpectedTime.plus(amountToAdd, chronoUnit), timeZoneId);
                assertThat(time.plus(amountToAdd, chronoUnit)).isEqualTo(expectedTime);
            } else {
                assertThatThrownBy(() -> time.plus(1, chronoUnit), "Testing ChronoUnit %s", chronoUnit)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testMinusMethodWithTemporalAmountNull() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).minus(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testMinusMethodWithDuration() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        final int subtractedHours = 31;
        final Duration duration = Duration.ofHours(subtractedHours);
        final Temporal updatedTime = zonedTime.minus(duration);
        assertThat(updatedTime).isInstanceOf(FEELZonedTime.class);
        final Temporal expectedTime = zonedTime.with(ChronoField.HOUR_OF_DAY, localTime.getHour() - (subtractedHours - 24));
        assertThat(updatedTime).isEqualTo(expectedTime);
    }

    @Test
    public void testMinusMethodWithPeriod() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        final Period period = Period.ofMonths(3);
        final Temporal updatedTime = zonedTime.minus(period);
        assertThat(updatedTime).isInstanceOf(FEELZonedTime.class);
        assertThat(updatedTime).isEqualTo(zonedTime);
    }

    @Test
    public void testMinusMethodWithChronoUnitNull() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).minus(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testMinusMethodWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        testMinusMethodWithChronoUnitParams(zonedTime, localTime, 2, zoneRegion);
    }

    @Test
    public void testMinusMethodWithZoneRegionTimeNegativeAmountToSubtract() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        testMinusMethodWithChronoUnitParams(zonedTime, localTime, -2, zoneRegion);
    }

    @Test
    public void testMinusMethodWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        testMinusMethodWithChronoUnitParams(zonedTime, localTime, 2, zoneOffset);
    }

    @Test
    public void testMinusMethodWithZoneOffsetTimeNegativeAmountToSubtract() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        testMinusMethodWithChronoUnitParams(zonedTime, localTime, -2, zoneOffset);
    }

    private void testMinusMethodWithChronoUnitParams(final FEELZonedTime time, final LocalTime baseForExpectedTime, final long amountToSubtract, final ZoneId timeZoneId) {
        for (ChronoUnit chronoUnit: ChronoUnit.values()) {
            if (chronoUnit.isTimeBased()) {
                final FEELZonedTime expectedTime = FEELZonedTime.of(baseForExpectedTime.minus(amountToSubtract, chronoUnit), timeZoneId);
                assertThat(time.minus(amountToSubtract, chronoUnit)).isEqualTo(expectedTime);
            } else {
                assertThatThrownBy(() -> time.minus(1, chronoUnit), "Testing ChronoUnit %s", chronoUnit)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testUntilMethodWithNullParameters() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).until(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testUntilMethodWithNullTemporalUnit() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).until(localTime, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testUntilMethodWithNullEndParameter() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).until(null, ChronoUnit.HOURS)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testUntilMethodWithZoneRegionTime() {
        final FEELZonedTime time = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoUnit chronoUnit: ChronoUnit.values()) {
            if (chronoUnit.isTimeBased()) {
                final LocalTime endLocalTime = localTime.plus(2, chronoUnit);
                final FEELZonedTime endTime = FEELZonedTime.of(endLocalTime, zoneRegion);
                assertThat(time.until(endTime, chronoUnit)).as("Testing ChronoUnit %s", chronoUnit).isEqualTo(localTime.until(endLocalTime, chronoUnit));
            } else {
                assertThatThrownBy(() -> time.until(localTime, chronoUnit), "Testing ChronoUnit %s", chronoUnit)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testGetLongWithNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).getLong(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetLongWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThat(zonedTime.getLong(chronoField)).isEqualTo(localTime.getLong(chronoField));
            } else {
                assertThatThrownBy(() -> zonedTime.getLong(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testGetLongWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThat(zonedTime.getLong(chronoField)).isEqualTo(localTime.getLong(chronoField));
            } else if (chronoField == ChronoField.OFFSET_SECONDS) {
                assertThat(zonedTime.getLong(chronoField)).isEqualTo(zoneOffset.getTotalSeconds());
            } else {
                assertThatThrownBy(() -> zonedTime.getLong(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testRangeWithNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).range(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testRangeWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThat(zonedTime.range(chronoField)).isEqualTo(localTime.range(chronoField));
            } else {
                assertThatThrownBy(() -> zonedTime.range(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testRangeWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()) {
                assertThat(zonedTime.range(chronoField)).isEqualTo(localTime.range(chronoField));
            } else if (chronoField == ChronoField.OFFSET_SECONDS) {
                assertThat(zonedTime.range(chronoField)).isEqualTo(zoneOffset.range(chronoField));
            } else {
                assertThatThrownBy(() -> zonedTime.range(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testGetMethodWithNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).get(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetMethodWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()
                    && chronoField != ChronoField.NANO_OF_DAY
                    && chronoField != ChronoField.MICRO_OF_DAY) {
                assertThat(zonedTime.get(chronoField)).isEqualTo(localTime.get(chronoField));
            } else {
                assertThatThrownBy(() -> zonedTime.get(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testGetMethodWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        for (ChronoField chronoField: ChronoField.values()) {
            if (chronoField.isTimeBased()
                    && chronoField != ChronoField.NANO_OF_DAY
                    && chronoField != ChronoField.MICRO_OF_DAY) {
                assertThat(zonedTime.get(chronoField)).isEqualTo(localTime.get(chronoField));
            } else if (chronoField == ChronoField.OFFSET_SECONDS) {
                assertThat(zonedTime.get(chronoField)).isEqualTo(zoneOffset.getTotalSeconds());
            } else {
                assertThatThrownBy(() -> zonedTime.get(chronoField), "Testing ChronoField %s", chronoField)
                        .isInstanceOf(DateTimeException.class);
            }
        }
    }

    @Test
    public void testQueryWithNullParameter() {
        assertThatThrownBy(() -> FEELZonedTime.of(localTime, zoneOffset).query(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testQueryWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertThat(zonedTime.query(TemporalQueries.chronology())).isNull();
        assertThat(zonedTime.query(TemporalQueries.localDate())).isNull();
        assertThat(zonedTime.query(TemporalQueries.localTime())).isEqualTo(localTime);
        assertThat(zonedTime.query(TemporalQueries.zoneId())).isEqualTo(zoneRegion);
        assertThat(zonedTime.query(TemporalQueries.zone())).isEqualTo(zoneRegion);
        assertThat(zonedTime.query(TemporalQueries.offset())).isNotNull();
        assertThat(zonedTime.query(TemporalQueries.precision())).isEqualTo(localTime.query(TemporalQueries.precision()));
    }

    @Test
    public void testQueryWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zonedTime.query(TemporalQueries.chronology())).isNull();
        assertThat(zonedTime.query(TemporalQueries.localDate())).isNull();
        assertThat(zonedTime.query(TemporalQueries.localTime())).isEqualTo(localTime);
        assertThat(zonedTime.query(TemporalQueries.zoneId())).isEqualTo(zoneOffset);
        assertThat(zonedTime.query(TemporalQueries.zone())).isEqualTo(zoneOffset);
        assertThat(zonedTime.query(TemporalQueries.offset())).isEqualTo(zoneOffset);
        assertThat(zonedTime.query(TemporalQueries.precision())).isEqualTo(localTime.query(TemporalQueries.precision()));
    }

    @Test
    public void testEqualsNullParameter() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zonedTime.equals(null)).isFalse();
    }

    @Test
    public void testEqualsZonedRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertThat(zonedTime).isEqualTo(FEELZonedTime.of(localTime, zoneRegion));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(localTime, ZoneId.of("Europe/Berlin")));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(LocalTime.of(2,3,4,9), zoneRegion));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(LocalTime.of(2,3,4,9), ZoneId.of("Europe/Berlin")));
    }

    @Test
    public void testEqualsZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zonedTime).isEqualTo(FEELZonedTime.of(localTime, zoneOffset));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(localTime, ZoneOffset.of("+07:00")));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(LocalTime.of(2,3,4,9), zoneOffset));
        assertThat(zonedTime).isNotEqualTo(FEELZonedTime.of(LocalTime.of(2,3,4,9), ZoneOffset.of("+07:00")));
    }

    @Test
    public void testEqualsZoneRegionTimeWithZoneOffsetTime() {
        final FEELZonedTime zoneRegionTime = FEELZonedTime.of(localTime, zoneRegion);
        final FEELZonedTime zoneOffsetTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zoneRegionTime).isNotEqualTo(zoneOffsetTime);
    }

    @Test
    public void testHashCodeWithZonedRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertThat(zonedTime.hashCode()).isEqualTo(localTime.hashCode() ^ zoneRegion.hashCode());
    }

    @Test
    public void testHashCodeWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zonedTime.hashCode()).isEqualTo(localTime.hashCode() ^ zoneOffset.hashCode());
    }

    @Test
    public void testToStringWithZoneRegionTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneRegion);
        assertThat(zonedTime.toString()).isEqualTo(localTime.toString() + "@" + zoneRegion.toString());
    }

    @Test
    public void testToStringWithZoneOffsetTime() {
        final FEELZonedTime zonedTime = FEELZonedTime.of(localTime, zoneOffset);
        assertThat(zonedTime.toString()).isEqualTo(localTime.toString() + zoneOffset.toString());
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