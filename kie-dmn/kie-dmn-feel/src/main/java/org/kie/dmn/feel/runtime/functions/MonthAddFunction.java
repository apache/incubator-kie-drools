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

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class MonthAddFunction
        extends BaseFEELFunction {

    public MonthAddFunction() {
        super( "monthAdd" );
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("datestring") String datetime, @ParameterName( "months to add" ) BigDecimal months) {
        if ( datetime == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "cannot be null" ) );
        }
        if ( months == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "months to add", "cannot be null" ) );
        }

        try {
            Object r = null;
            if( datetime.contains( "T" ) ) {
                r = BuiltInFunctions.getFunction( DateAndTimeFunction.class ).invoke( datetime ).cata( BuiltInType.justNull(), Function.identity() );
            } else {
                r = BuiltInFunctions.getFunction( DateFunction.class ).invoke( datetime ).cata( BuiltInType.justNull(), Function.identity() );
            }

            if ( r != null && r instanceof TemporalAccessor ) {
                return invoke( (TemporalAccessor) r, months );
            } else {
                return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "date-parsing exception" ) );
            }
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "date-parsing exception", e ) );
        }
    }

    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("datetime") TemporalAccessor datetime, @ParameterName( "months to add" ) BigDecimal months) {
        if ( datetime == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datetime", "cannot be null" ) );
        }
        if ( months == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "months to add", "cannot be null" ) );
        }

        try {
            if( datetime instanceof Temporal ) {
                return FEELFnResult.ofResult( ((Temporal) datetime).plus( months.longValue(), ChronoUnit.MONTHS ) );
            } else {
                return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datetime", "invalid 'date' or 'date and time' parameter" ) );
            }
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datetime", "invalid 'date' or 'date and time' parameter", e ) );
        }
    }

}
