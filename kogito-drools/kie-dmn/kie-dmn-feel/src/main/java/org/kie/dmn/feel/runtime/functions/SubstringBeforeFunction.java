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

public class SubstringBeforeFunction
        extends BaseFEELFunction {

    public SubstringBeforeFunction() {
        super( "substring before" );
    }

    public FEELFnResult<String> invoke(@ParameterName("string") String string, @ParameterName("match") String match) {
        if ( string == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "string", "cannot be null" ) );
        }
        if ( match == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "match", "cannot be null" ) );
        }

        int index = string.indexOf( match );
        if ( index > 0 ) {
            return FEELFnResult.ofResult( string.substring( 0, index ) );
        } else {
            return FEELFnResult.ofResult( "" );
        }
    }

}
