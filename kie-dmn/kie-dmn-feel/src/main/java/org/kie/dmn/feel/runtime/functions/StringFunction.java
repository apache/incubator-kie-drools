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

public class StringFunction
        extends BaseFEELFunction {

    public StringFunction() {
        super( "string" );
    }

    public FEELFnResult<String> invoke(@ParameterName("from") Object val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "val", "cannot be null"));
        } else {
            return FEELFnResult.ofResult( val.toString() );
        }
    }

    public FEELFnResult<String> invoke(@ParameterName( "mask" ) String mask, @ParameterName("p") Object[] params) {
        if ( mask == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "mask", "cannot be null"));
        } else {
            return FEELFnResult.ofResult( String.format( mask, params ) );
        }
    }

}
