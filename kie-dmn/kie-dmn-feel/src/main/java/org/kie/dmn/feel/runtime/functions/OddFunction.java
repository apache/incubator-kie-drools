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

package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.math.BigDecimal;

public class OddFunction
        extends BaseFEELFunction {
    public static final OddFunction INSTANCE = new OddFunction();

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    OddFunction() {
        super("odd");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot be null" ) );
        }
        if ( number.remainder( BigDecimal.ONE ).signum() != 0 ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot have non-zero fractional part" ) );
        }
        return FEELFnResult.ofResult( BigDecimal.ZERO.compareTo( number.remainder(TWO) ) != 0);
    }
}
