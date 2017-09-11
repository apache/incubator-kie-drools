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

import java.util.Arrays;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.TypeUtil;

public class AllFunction
        extends BaseFEELFunction {

    public AllFunction() {
        super( "all" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "list" ) List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        if (!TypeUtil.isCollectionTypeHomogenous(list, Boolean.class)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "an element in the list is not a Boolean"));
        }
        for ( final Object element : list ) {
            if (element == null || element == Boolean.FALSE) {
                return FEELFnResult.ofResult( false );
            }
        }
        return FEELFnResult.ofResult( true );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "list" ) Boolean single) {
        return FEELFnResult.ofResult( single );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "b" ) Object[] list) {
        if ( list == null ) { 
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "b", "cannot be null"));
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
