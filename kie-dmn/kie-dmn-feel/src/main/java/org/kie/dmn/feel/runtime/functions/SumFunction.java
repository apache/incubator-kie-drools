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
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.util.EvalHelper;

public class SumFunction
        extends BaseFEELFunction {

    public SumFunction() {
        super( "sum" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List list) {
        BigDecimal sum = BigDecimal.ZERO;
        for ( Object element : list ) {
            if ( element instanceof BigDecimal ) {
                sum = sum.add( (BigDecimal) element );
            } else if ( element instanceof Number ) {
                sum = sum.add( EvalHelper.getBigDecimalOrNull( element ) );
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not suitable for the sum"));
            }
        }
        return FEELFnResult.ofResult( sum );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Number single) {
        if ( single == null ) { 
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "the single value list cannot be null"));
        }
        
        if( single instanceof BigDecimal ) {
            return FEELFnResult.ofResult((BigDecimal) single );
        } 
        BigDecimal result = EvalHelper.getBigDecimalOrNull( single );
        if ( result != null ) {
            return FEELFnResult.ofResult( result );
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "single element in list not a number"));
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) { 
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "the single value list cannot be null"));
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
