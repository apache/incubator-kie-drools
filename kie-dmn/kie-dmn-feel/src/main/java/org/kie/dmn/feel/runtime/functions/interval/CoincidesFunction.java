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

package org.kie.dmn.feel.runtime.functions.interval;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class CoincidesFunction
        extends BaseFEELFunction {

    public static final CoincidesFunction INSTANCE = new CoincidesFunction();

    public CoincidesFunction() {
        super( "coincides" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "value1" ) Comparable value1, @ParameterName( "value2" ) Comparable value2) {
        if ( value1 == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "value1", "cannot be null"));
        }
        if ( value2 == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "value2", "cannot be null"));
        }
        try {
            boolean result = value1.compareTo( value2 ) == 0;
            return FEELFnResult.ofResult( result );
        } catch( Exception e ) {
            // values are not comparable
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "value1", "cannot be compared to value2"));
        }
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "range1" ) Range range1, @ParameterName( "range2" ) Range range2) {
        if ( range1 == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "range1", "cannot be null"));
        }
        if ( range2 == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "range2", "cannot be null"));
        }
        try {
            boolean result = range1.equals( range2 );
            return FEELFnResult.ofResult( result );
        } catch( Exception e ) {
            // values are not comparable
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "range1", "cannot be compared to range2"));
        }
    }

}
