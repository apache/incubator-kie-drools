/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.FEELNumberFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.NumberEvalHelper;

public class DecimalFunction
        extends BaseFEELFunction implements FEELNumberFunction {

    public static final DecimalFunction INSTANCE = new DecimalFunction();

    private DecimalFunction() {
        super( "decimal" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n, @ParameterName( "scale" ) BigDecimal scale) {
        if ( n == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "cannot be null"));
        }
        if ( scale == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "scale", "cannot be null"));
        }
        Object scaleObj = NumberEvalHelper.coerceIntegerNumber(scale);
        // Based on Table 76: Semantics of numeric functions, the scale is in range −6111 .. 6176
        int scaleInt = 0;
        if (scaleObj instanceof Integer) {
            scaleInt = (Integer) scaleObj;
            if (scaleInt < -6111 || scaleInt > 6176) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "scale", "must be in range between -6111 to 6176."));
            }
        }
        return FEELFnResult.ofResult(n.setScale(scaleInt, RoundingMode.HALF_EVEN));
    }
}
