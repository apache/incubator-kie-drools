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

public class DateTimeFunction
        extends BaseFEELFunction {

    public DateTimeFunction() {
        super( "date and time" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "from" ) String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            return FEELFnResult.ofResult( DateTimeFormatter.ISO_DATE_TIME.parseBest( val, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from ) );
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
}
