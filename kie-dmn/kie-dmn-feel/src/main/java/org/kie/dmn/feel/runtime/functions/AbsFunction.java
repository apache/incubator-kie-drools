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
import java.time.Duration;
import java.time.Period;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AbsFunction
        extends BaseFEELFunction {
    public static final AbsFunction INSTANCE = new AbsFunction();

    AbsFunction() {
        super( "abs" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "number", "cannot be null"));
        }
        return FEELFnResult.ofResult( number.abs() );
    }

    public FEELFnResult<Period> invoke(@ParameterName( "n" ) Period duration) {
        if ( duration == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "duration", "cannot be null"));
        }
        return FEELFnResult.ofResult( duration.toTotalMonths() < 0 ? duration.negated() : duration );
    }

    public FEELFnResult<Duration> invoke(@ParameterName( "n" ) Duration duration) {
        if ( duration == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "duration", "cannot be null"));
        }
        return FEELFnResult.ofResult( duration.abs() );
    }

}
