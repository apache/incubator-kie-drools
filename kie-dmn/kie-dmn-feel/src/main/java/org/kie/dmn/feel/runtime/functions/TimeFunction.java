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

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class TimeFunction
        extends BaseFEELFunction {

    public static final DateTimeFormatter FEEL_TIME;
    static {
        FEEL_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                  .append(DateTimeFormatter.ISO_LOCAL_TIME)
                                                  .optionalStart()
                                                  .appendLiteral("@")
                                                  .appendZoneRegionId()
                                                  .optionalEnd()
                                                  .optionalStart()
                                                  .appendOffsetId()
                                                  .optionalEnd()
                                                  .toFormatter();
    }

    public TimeFunction() {
        super( "time" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("from") String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            TemporalAccessor parsed = FEEL_TIME.parse(val);

            if (parsed.query(TemporalQueries.offset()) != null) {
                // it is an offset-zoned time, so I can know for certain an OffsetTime
                OffsetTime asOffSetTime = parsed.query(OffsetTime::from);
                return FEELFnResult.ofResult(asOffSetTime);
            } else if (parsed.query(TemporalQueries.zone()) == null) {
                // if it does not contain any zone information at all, then I know for certain is a local time.
                LocalTime asLocalTime = parsed.query(LocalTime::from);
                return FEELFnResult.ofResult(asLocalTime);
            }

            return FEELFnResult.ofResult(parsed);
        } catch (DateTimeException e) {
            // try to parse it as a date time and extract the date component
            // NOTE: this is an extension to the standard
            return BuiltInFunctions.getFunction( DateAndTimeFunction.class ).invoke( val )
                .cata( overrideLeft -> FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "time-parsing exception", e)),
                       r -> invoke( r )
                       );
        }
    }

    private static final BigDecimal NANO_MULT = BigDecimal.valueOf( 1000000000 );

    public FEELFnResult<TemporalAccessor> invoke(
            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
            @ParameterName("second") Number seconds, @ParameterName("offset") Duration offset) {
        if ( hour == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "hour", "cannot be null"));
        }
        if ( minute == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "minute", "cannot be null"));
        }
        if ( seconds == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "seconds", "cannot be null"));
        }
        
        try {
            int nanosecs = 0;
            if( seconds instanceof BigDecimal ) {
                BigDecimal secs = (BigDecimal) seconds;
                nanosecs = secs.subtract( secs.setScale( 0, BigDecimal.ROUND_DOWN ) ).multiply( NANO_MULT ).intValue();
            }

            if ( offset == null ) {
                return FEELFnResult.ofResult( LocalTime.of( hour.intValue(), minute.intValue(), seconds.intValue(),
                                                            nanosecs ) );
            } else {
                return FEELFnResult.ofResult( OffsetTime.of( hour.intValue(), minute.intValue(), seconds.intValue(),
                                                             nanosecs,
                                              ZoneOffset.ofTotalSeconds( (int) offset.getSeconds() ) ) );
            }
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "time-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("from") TemporalAccessor date) {
        if ( date == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            if( date.query( TemporalQueries.offset() ) == null ) {
                return FEELFnResult.ofResult( LocalTime.from( date ) );
            } else {
                return FEELFnResult.ofResult( OffsetTime.from( date ) );
            }
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "time-parsing exception", e));
        }
    }

}
