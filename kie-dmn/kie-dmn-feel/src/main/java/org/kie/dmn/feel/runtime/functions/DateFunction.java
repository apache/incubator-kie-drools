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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DateFunction
        extends BaseFEELFunction {

    public DateFunction() {
        super( "date" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "from" ) String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            return FEELFnResult.ofResult( LocalDate.from( DateTimeFormatter.ISO_DATE.parse( val ) ) );
        } catch (DateTimeException e) {
            // try to parse it as a date time and extract the date component
            // NOTE: this is an extension to the standard
            return BuiltInFunctions.getFunction( DateAndTimeFunction.class ).invoke( val )
                .cata( overrideLeft -> FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "date-parsing exception", e)),
                       r -> invoke( r )
                       );   
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "year" ) Number year, @ParameterName( "month" ) Number month, @ParameterName( "day" ) Number day) {
        if ( year == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "year", "cannot be null"));
        }
        if ( month == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "month", "cannot be null"));
        }
        if ( day == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "day", "cannot be null"));
        }
        
        try {
            return FEELFnResult.ofResult( LocalDate.of( year.intValue(), month.intValue(), day.intValue() ) );
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input parameters date-parsing exception", e));
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName( "from" ) TemporalAccessor date) {
        if ( date == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        
        try {
            return FEELFnResult.ofResult( LocalDate.from( date ) );
        } catch (DateTimeException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "date-parsing exception", e));
        }
    }



}
