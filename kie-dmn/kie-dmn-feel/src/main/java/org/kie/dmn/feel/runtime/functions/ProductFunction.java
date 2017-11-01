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
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class ProductFunction
        extends BaseFEELFunction {

    public ProductFunction() {
        super( "product" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "the list cannot be null"));
        }
        BigDecimal product = list.isEmpty() ? BigDecimal.ZERO : BigDecimal.ONE;
        for ( Object element : list ) {
            if ( element instanceof BigDecimal ) {
                product = product.multiply( (BigDecimal) element );
            } else if ( element instanceof Number ) {
                product = product.multiply( EvalHelper.getBigDecimalOrNull( element ) );
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not suitable for the product"));
            }
        }
        return FEELFnResult.ofResult( product );
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
