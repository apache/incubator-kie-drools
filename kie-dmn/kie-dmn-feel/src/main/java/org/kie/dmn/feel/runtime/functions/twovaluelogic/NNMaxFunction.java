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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.util.Arrays;
import java.util.List;

public class NNMaxFunction
        extends BaseFEELFunction {

    public static final NNMaxFunction INSTANCE = new NNMaxFunction();

    public NNMaxFunction() {
        super( "nn max" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List list) {
        if ( list == null || list.isEmpty() ) {
            return FEELFnResult.ofResult( null );
        } else {
            try {
                Comparable max = null;
                for( int i = 0; i < list.size(); i++ ) {
                    Comparable candidate = (Comparable) list.get( i );
                    if( candidate == null ) {
                        continue;
                    } else if( max == null ) {
                        max = candidate;
                    } else if( max.compareTo( candidate ) < 0 ) {
                        max = candidate;
                    }
                }
                return FEELFnResult.ofResult( max );
            } catch (ClassCastException e) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "contains items that are not comparable"));
            }
        }
    }

    public FEELFnResult<Object> invoke(@ParameterName("c") Object[] list) {
        if ( list == null || list.length == 0 ) {
            return FEELFnResult.ofResult( null );
        }
        
        return invoke( Arrays.asList( list ) );
    }

}
