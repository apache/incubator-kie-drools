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
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SortFunction
        extends BaseFEELFunction {

    public SortFunction() {
        super( "sort" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName( "ctx" ) EvaluationContext ctx,
                                             @ParameterName("list") List list,
                                             @ParameterName("precedes") FEELFunction function) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        } else if ( function == null ){
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "precedes", "cannot be null"));
        }
        List<Object> newList = new ArrayList<Object>( list );
        AtomicBoolean hasError = new AtomicBoolean( false );
        newList.sort( (a, b) -> {
            try {
                Object result = function.invokeReflectively( ctx, new Object[] {a, b} );
                if( !(result instanceof Boolean) || ((Boolean)result) == true ) {
                    return -1;
                } else {
                    return 1;
                }
            } catch ( Throwable t ) {
                hasError.set( true );
                return 0;
            }
        } );
        if( hasError.get() ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "precedes", "raised an exception while sorting list "+list ) );
        } else {
            return FEELFnResult.ofResult( newList );
        }
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        List<Object> newList = new ArrayList<Object>( list );
        AtomicBoolean hasError = new AtomicBoolean( false );
        newList.sort( (a, b) -> {
            try {
                if( a instanceof Comparable && b instanceof Comparable ) {
                    return ((Comparable)a).compareTo( b );
                } else {
                    return 0;
                }
            } catch ( Throwable t ) {
                // we might need to capture the exception for error reporting as well
                hasError.set( true );
                return 0;
            }
        } );
        if( hasError.get() ) {
            return FEELFnResult.ofError( new InvalidParametersEvent(Severity.ERROR, "list", "raised an exception while sorting by natural order" ) );
        } else {
            return FEELFnResult.ofResult( newList );
        }
    }

}
