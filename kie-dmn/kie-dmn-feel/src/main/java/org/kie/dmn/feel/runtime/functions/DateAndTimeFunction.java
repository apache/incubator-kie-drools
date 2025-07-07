/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.FEELDateTimeFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static org.kie.dmn.feel.util.NumberEvalHelper.coerceIntegerNumber;

public class DateAndTimeFunction
        extends BaseFEELFunction implements FEELDateTimeFunction {

    public static final DateAndTimeFunction INSTANCE = new DateAndTimeFunction();

    public static final DateTimeFormatter FEEL_DATE_TIME;
    public static final DateTimeFormatter REGION_DATETIME_FORMATTER;
    static {
        FEEL_DATE_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                       .append(DateFunction.FEEL_DATE)
                                                       .appendLiteral('T')
                                                       .append(TimeFunction.FEEL_TIME)
                                                       .toFormatter();
        REGION_DATETIME_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                                 .append(DateFunction.FEEL_DATE)
                                                                 .appendLiteral('T')
                                                                 .append(DateTimeFormatter.ISO_LOCAL_TIME)
                                                                 .appendLiteral("@")
                                                                 .appendZoneRegionId()
                                                                 .toFormatter();
    }


    private DateAndTimeFunction() {
        super(FEELConversionFunctionNames.DATE_AND_TIME);
    }

    static TemporalAccessor getValidDate(TemporalAccessor date) {
        if (date == null) {
            throw new IllegalArgumentException("Parameter 'date' is missing or invalid.");
        }
        if (date instanceof LocalDate) {
            return date;
        }
        // FEEL Spec Table 58 "date is a date or date time [...] creates a date time from the given date (ignoring any time component)" [that means ignoring any TZ from `date` parameter, too]
        // I try to convert `date` to a LocalDate, if the query method returns null would signify conversion is not possible.
        date = date.query(TemporalQueries.localDate());
        if (date != null) {
            return date;
        }
        throw new IllegalArgumentException("Parameter 'date' is missing or invalid.");
    }

    static TemporalAccessor getValidTime(TemporalAccessor time) {
        if (time == null || !(time instanceof LocalTime || (time.query(TemporalQueries.localTime()) != null && time.query(TemporalQueries.zone()) != null))) {
            throw new IllegalArgumentException("Parameter 'time' is missing or invalid.");
        }
        return time;
    }

    static ZoneId getValidTimeZone(String timeZone) {
        if (timeZone == null || timeZone.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'timezone' is missing or invalid.");
        }
        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Parameter 'timezone' is missing or invalid.");
        }
    }

    static FEELFnResult<TemporalAccessor> generateDateTimeAndTimezone(TemporalAccessor date, TemporalAccessor time, ZoneId zoneId) {
        try {
            TemporalAccessor validatedDate = getValidDate(date);
            TemporalAccessor validatedTime = getValidTime(time);
            if (validatedDate instanceof LocalDate && validatedTime instanceof LocalTime) {
                if (zoneId != null) {
                    return FEELFnResult.ofResult(ZonedDateTime.of((LocalDate) validatedDate, (LocalTime) validatedTime, zoneId));
                } else {
                    return FEELFnResult.ofResult(LocalDateTime.of((LocalDate) validatedDate, (LocalTime) validatedTime));
                }
            } else if (validatedDate instanceof LocalDate && time.query(TemporalQueries.localTime()) != null && time.query(TemporalQueries.zone()) != null) {
                return FEELFnResult.ofResult(ZonedDateTime.of((LocalDate) validatedDate, LocalTime.from(validatedTime), zoneId != null ? zoneId : ZoneId.from(validatedTime)));
            }
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "cannot invoke function for the input parameters"));
        } catch (IllegalArgumentException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "Invalid Input", e.getMessage()));
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }

    static FEELFnResult<TemporalAccessor> generateDateTimeAndTimezone(TemporalAccessor date, TemporalAccessor time) {
        return generateDateTimeAndTimezone(date, time, null);
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "from" ) String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        if (!DateFunction.BEGIN_YEAR.matcher(val).find()) { // please notice the regex strictly requires the beginning, so we can use find.
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "year not compliant with XML Schema Part 2 Datatypes"));
        }

        try {
            if( val.contains( "T" ) ) {
                return FEELFnResult.ofResult(FEEL_DATE_TIME.parseBest(val, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from));
            } else {
                LocalDate value = DateTimeFormatter.ISO_DATE.parse(val, LocalDate::from);
                return FEELFnResult.ofResult( LocalDateTime.of(value, LocalTime.of(0, 0)));
            }
        } catch ( Exception e ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("date") TemporalAccessor date, @ParameterName("time") TemporalAccessor time) {
        return generateDateTimeAndTimezone(date, time);
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
                                                 @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second ) {
        return invoke( year, month, day, hour, minute, second, (Number) null );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("year") Number year, @ParameterName("month") Number month, @ParameterName("day") Number day,
                                                 @ParameterName("hour") Number hour, @ParameterName("minute") Number minute, @ParameterName("second") Number second,
                                                 @ParameterName("hour offset") Number hourOffset) {
        try {
            int coercedYear = coerceIntegerNumber(year).orElseThrow(() -> new NoSuchElementException("year"));
            int coercedMonth = coerceIntegerNumber(month).orElseThrow(() -> new NoSuchElementException("month"));
            int coercedDay = coerceIntegerNumber(day).orElseThrow(() -> new NoSuchElementException("day"));
            int coercedHour = coerceIntegerNumber(hour).orElseThrow(() -> new NoSuchElementException("hour"));
            int coercedMinute = coerceIntegerNumber(minute).orElseThrow(() -> new NoSuchElementException("minute"));
            int coercedSecond = coerceIntegerNumber(second).orElseThrow(() -> new NoSuchElementException("second"));

            if (hourOffset != null) {
                Optional<Integer> coercedHourOffset = coerceIntegerNumber(hourOffset);
                return coercedHourOffset.<FEELFnResult<TemporalAccessor>>map(integer -> FEELFnResult.ofResult(
                        OffsetDateTime.of(
                                coercedYear, coercedMonth, coercedDay,
                                coercedHour, coercedMinute, coercedSecond,
                                0, ZoneOffset.ofHours(integer)))).orElseGet(() -> FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "hour offset", "could not be coerced to Integer")));

            } else {
                return FEELFnResult.ofResult(
                        LocalDateTime.of(
                                coercedYear, coercedMonth, coercedDay,
                                coercedHour, coercedMinute, coercedSecond
                        )
                );
            }
        } catch (NoSuchElementException e) { // thrown by Optional.orElseThrow()
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, e.getMessage(), "could not be coerced to Integer: either null or not a valid Number."));
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("year") Number year, @ParameterName("month") Number month, @ParameterName("day") Number day,
                                                 @ParameterName("hour") Number hour, @ParameterName("minute") Number minute, @ParameterName("second") Number second,
                                                 @ParameterName("timezone") String timezone) {
        try {
            int coercedYear = coerceIntegerNumber(year).orElseThrow(() -> new NoSuchElementException("year"));
            int coercedMonth = coerceIntegerNumber(month).orElseThrow(() -> new NoSuchElementException("month"));
            int coercedDay = coerceIntegerNumber(day).orElseThrow(() -> new NoSuchElementException("day"));
            int coercedHour = coerceIntegerNumber(hour).orElseThrow(() -> new NoSuchElementException("hour"));
            int coercedMinute = coerceIntegerNumber(minute).orElseThrow(() -> new NoSuchElementException("minute"));
            int coercedSecond = coerceIntegerNumber(second).orElseThrow(() -> new NoSuchElementException("second"));
            return FEELFnResult.ofResult(ZonedDateTime.of(coercedYear, coercedMonth, coercedDay,
                    coercedHour, coercedMinute, coercedSecond, 0, TimeZone.getTimeZone(timezone).toZoneId()));
        } catch (NoSuchElementException e) { // thrown by Optional.orElseThrow()
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, e.getMessage(), "could not be coerced to Integer: either null or not a valid Number."));
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("date") TemporalAccessor date, @ParameterName("time") TemporalAccessor time, @ParameterName("timeZone") String timeZone) {
        try {
            ZoneId zoneId = getValidTimeZone(timeZone);
            return generateDateTimeAndTimezone(date, time, zoneId);
        } catch (IllegalArgumentException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "Invalid Input", e.getMessage()));
        }
    }

}
