/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class DateAndTimeFunction
        extends BaseFEELFunction {

    public DateAndTimeFunction() {
        super( "date and time" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "from" ) String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            if( val.contains( "T" ) ) {
                return FEELFnResult.ofResult( DateTimeFormatter.ISO_DATE_TIME.parseBest( val, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from ) );
            } else {
                TemporalAccessor value = DateTimeFormatter.ISO_DATE.parse( val, LocalDate::from );
                return FEELFnResult.ofResult( LocalDateTime.of( (LocalDate)value, LocalTime.of( 0, 0 ) ) );
            }
        } catch ( Exception e ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "date" ) Temporal date, @ParameterName( "time" ) Temporal time) {
        if ( date == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "cannot be null"));
        }
        if ( !(date instanceof LocalDate) ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "must be an instance of LocalDate"));
        }
        if ( time == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "time", "cannot be null"));
        }
        if ( !(time instanceof LocalTime || time instanceof OffsetTime) ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "time", "must be an instance of LocalTime or OffsetTime"));
        }
        
        try {
            if( date instanceof LocalDate && time instanceof LocalTime ) {
                return FEELFnResult.ofResult( LocalDateTime.of( (LocalDate) date, (LocalTime) time ) );
            } else if( date instanceof LocalDate && time instanceof OffsetTime ) {
                return FEELFnResult.ofResult( ZonedDateTime.of( (LocalDate) date, LocalTime.from( time ), ZoneOffset.from( time ) ) );
            }
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "cannot invoke function for the input parameters"));
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
                                                 @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second ) {
        return invoke( year, month, day, hour, minute, second, null );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day,
                                                 @ParameterName( "hour" ) Number hour, @ParameterName( "minute" ) Number minute, @ParameterName( "second" ) Number second,
                                                 @ParameterName( "hour offset" ) Number hourOffset ) {
        if ( year == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "year", "cannot be null"));
        }
        if ( month == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "month", "cannot be null"));
        }
        if ( day == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "day", "cannot be null"));
        }
        if ( hour == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "hour", "cannot be null"));
        }
        if ( minute == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "minute", "cannot be null"));
        }
        if ( second == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "second", "cannot be null"));
        }

        try {
            if( hourOffset != null ) {
                return FEELFnResult.ofResult( ZonedDateTime.of( year.intValue(), month.intValue(), day.intValue(),
                                                                hour.intValue(), minute.intValue(), second.intValue(),
                                                                0, ZoneOffset.ofHours( hourOffset.intValue() ) ) );
            } else {
                return FEELFnResult.ofResult( LocalDateTime.of( year.intValue(), month.intValue(), day.intValue(),
                                                                hour.intValue(), minute.intValue(), second.intValue() ) );
            }
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }


}
