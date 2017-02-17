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

public class ReplaceFunction
        extends BaseFEELFunction {

    public ReplaceFunction() {
        super( "replace" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern,
                                       @ParameterName( "replacement" ) String replacement ) {
        if ( input == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "input", "cannot be null" ) );
        }
        if ( pattern == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "cannot be null" ) );
        }

        // for now, using standard java matches function
        return FEELFnResult.ofResult( input.replaceAll( pattern, replacement ) );
    }

    public FEELFnResult<Object> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern,
                                       @ParameterName( "replacement" ) String replacement, @ParameterName("flags") String flags) {
        if ( input == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "input", "cannot be null" ) );
        }
        if ( pattern == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "cannot be null" ) );
        }

        // for now, using standard java replaceAll function... needs fixing to support flags
        return FEELFnResult.ofResult( input.replaceAll( pattern, replacement ) );
    }

}
