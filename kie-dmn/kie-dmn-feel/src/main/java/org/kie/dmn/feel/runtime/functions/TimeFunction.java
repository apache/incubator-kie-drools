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
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class TimeFunction
        extends BaseFEELFunction {

    public TimeFunction() {
        super( "time" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("from") String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            return FEELFnResult.ofResult( DateTimeFormatter.ISO_TIME.parseBest( val, OffsetTime::from, LocalTime::from ) );
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "time-parsing exception", e));
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
