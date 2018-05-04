/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions.extended;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SqrtFunction
        extends BaseFEELFunction {
    public static final SqrtFunction INSTANCE = new SqrtFunction();

    private static final BigDecimal TWO = new BigDecimal( 2.0, MathContext.DECIMAL128 );

    SqrtFunction() {
        super("sqrt");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot be null" ) );
        }
        if ( number.signum() < 0 ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "is negative" ) );
        }
        return FEELFnResult.ofResult( sqrt( EvalHelper.getBigDecimalOrNull( number ) ) );
    }

    static BigDecimal sqrt( BigDecimal arg ) { // can be modified later to short-circuit if precision is not needed
        final BigDecimal low = new BigDecimal( Math.sqrt( arg.doubleValue() ), MathContext.DECIMAL128 ); // 16 decimal places
        final BigDecimal mid = low.add( arg.subtract( low.pow( 2, MathContext.DECIMAL128 ) ).divide( low.multiply( TWO ), RoundingMode.HALF_EVEN ) ); // 32 decimal places
        final BigDecimal high = mid.add( arg.subtract( mid.pow( 2, MathContext.DECIMAL128 ) ).divide( mid.multiply( TWO ), RoundingMode.HALF_EVEN ) ); // 34 decimal places
        return high;
    }
}
