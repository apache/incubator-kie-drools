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
package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.FEELNumberFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static org.kie.dmn.feel.util.NumberEvalHelper.coerceIntegerNumber;

public class FloorFunction
        extends BaseFEELFunction implements FEELNumberFunction {

    public static final FloorFunction INSTANCE = new FloorFunction();

    private FloorFunction() {
        super( "floor" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n) {
        return invoke(n, BigDecimal.ZERO);
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") BigDecimal n, @ParameterName("scale") BigDecimal scale) {
        try {
            int coercedN = coerceIntegerNumber(n).orElseThrow(() -> new NoSuchElementException("n"));
            int coercedScale = coerceIntegerNumber(scale).orElseThrow(() -> new NoSuchElementException("scale"));
            if (coercedScale < -6111 || coercedScale > 6176) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "scale", "must be in range between -6111 to 6176."));
            }
            return FEELFnResult.ofResult( n.setScale( coercedScale, RoundingMode.FLOOR ) );
        } catch (NoSuchElementException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, e.getMessage(), "could not be coerced to Integer: either null or not a valid Number."));
        }
    }
}
