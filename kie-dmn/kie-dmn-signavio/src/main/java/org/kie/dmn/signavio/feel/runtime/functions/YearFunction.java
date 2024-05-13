/**
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
package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;

public class YearFunction
        extends BaseFEELFunction {

    public YearFunction() {
        super( "year" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("datestring") String val) {
        if ( val == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "cannot be null" ) );
        }

        try {
            TemporalAccessor r = DateFunction.INSTANCE.invoke(val).cata(BuiltInType.justNull(), Function.identity());
            if (r instanceof TemporalAccessor) {
                return invoke(r);
            } else {
                return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "date-parsing exception" ) );
            }
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datestring", "date-parsing exception", e ) );
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("datetime") TemporalAccessor datetime) {
        if ( datetime == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datetime", "cannot be null" ) );
        }

        try {
            return FEELFnResult.ofResult(NumberEvalHelper.getBigDecimalOrNull(datetime.get(ChronoField.YEAR ) ) );
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "datetime", "invalid 'date' or 'date and time' parameter", e ) );
        }
    }

}
